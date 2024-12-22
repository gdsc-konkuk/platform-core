package gdsc.konkuk.platformcore.application.member;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.spy;

import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyDeletedException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import gdsc.konkuk.platformcore.fixture.member.MemberRegisterRequestFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class MemberServiceTest {

    private MemberService subject;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subject = new MemberService(memberRepository, attendanceRepository, participantRepository);
    }

    @Test
    @DisplayName("Register : 새로운 멤버 회원가입 성공")
    void should_success_when_newMember_register() {
        // given
        MemberRegisterRequest memberRegisterRequest = MemberRegisterRequestFixture.builder().build()
                .getFixture();
        Member memberToRegister = MemberFixture.builder().build().getFixture();
        given(memberRepository.findByStudentId(memberRegisterRequest.getStudentId())).willReturn(
                Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(memberToRegister);

        // when
        Member actual = subject.register(memberRegisterRequest);

        // then
        assertNotNull(actual);
    }

    @Test
    @DisplayName("Register : 이미 존재하는 멤버 회원가입 실패")
    void should_fail_when_already_exist_member_register() {
        // given
        MemberRegisterRequest memberRegisterRequest = MemberRegisterRequestFixture.builder().build()
                .getFixture();
        Member alreadyRegisteredMember = MemberFixture.builder().build().getFixture();
        given(memberRepository.findByStudentId(memberRegisterRequest.getStudentId()))
                .willReturn(Optional.of(alreadyRegisteredMember));

        // when
        Executable action = () -> subject.register(memberRegisterRequest);

        // then
        assertThrows(UserAlreadyExistException.class, action);
    }

    @Test
    @DisplayName("withdraw : 존재하는 멤버 탈퇴 성공")
    void should_success_when_user_exists() {
        // given
        Member memberToDelete = MemberFixture.builder().build().getFixture();
        given(memberRepository.findById(memberToDelete.getId())).willReturn(
                Optional.of(memberToDelete));

        // when
        subject.withdraw(memberToDelete.getId());

        // then
        assertTrue(memberToDelete.isMemberDeleted());
        assertNotNull(memberToDelete.getSoftDeletedAt());
    }

    @Test
    @DisplayName("withdraw : 존재하지 않는 멤버 탈퇴 실패")
    void should_fail_when_user_not_exists() {
        // given
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        Executable action = () -> subject.withdraw(0L);

        // then
        assertThrows(UserNotFoundException.class, action);
    }

    @Test
    @DisplayName("withdraw : 이미 삭제된 멤버 탈퇴 실패")
    void should_fail_when_user_already_deleted() {
        // given
        Member memberAlreadyDeleted = spy(MemberFixture.builder().build().getFixture());
        given(memberRepository.findById(memberAlreadyDeleted.getId())).willReturn(
                Optional.of(memberAlreadyDeleted));
        given(memberAlreadyDeleted.isMemberDeleted()).willReturn(true);

        // when `Member` soft deleted
        subject.withdraw(memberAlreadyDeleted.getId());
        Executable action = () -> subject.withdraw(memberAlreadyDeleted.getId());

        // then
        assertThrows(UserAlreadyDeletedException.class, action);
    }
}
