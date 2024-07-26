package gdsc.konkuk.platformcore.application.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyDeletedException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.member.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;

class MemberServiceTest {

  private MemberService subject;

  @Mock
  private Member member;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    subject = new MemberService(passwordEncoder, memberRepository, attendanceRepository);
  }

  @Test
  @DisplayName("Register : 새로운 멤버 회원가입 성공")
  void should_success_when_newMember_register() {
    // given
    MemberRegisterRequest memberRegisterRequest =
      MemberRegisterRequest.builder()
        .memberId("202011288")
        .password("password")
        .email("example@konkuk.ac.kr")
        .name("홍길동")
        .batch("24-25")
        .build();
    given(memberRepository.findByMemberId(any())).willReturn(Optional.empty());
    given(memberRepository.save(any(Member.class))).willReturn(member);
    given(passwordEncoder.encode(any())).willReturn("password");

    // when
    Member actual = subject.register(memberRegisterRequest);

    // then
    assertNotNull(actual);
  }

  @Test
  @DisplayName("Register : 이미 존재하는 멤버 회원가입 실패")
  void should_fail_when_already_exist_member_register() {
    // given
    MemberRegisterRequest memberRegisterRequest =
      MemberRegisterRequest.builder()
        .memberId("202011288")
        .password("password")
        .email("example@konkuk.ac.kr")
        .name("홍길동")
        .batch("24-25")
        .build();
    given(memberRepository.findByMemberId(any()))
      .willReturn(Optional.of(MemberRegisterRequest.toEntity(memberRegisterRequest)));

    // when
    Executable action = () -> subject.register(memberRegisterRequest);

    // then
    assertThrows(UserAlreadyExistException.class, action);
  }

  @Test
  @DisplayName("withdraw : 존재하는 멤버 탈퇴 성공")
  void should_success_when_user_exists() {
    // given
    Long targetId = 1L;
    Member member =
      Member.builder()
        .id(1L)
        .memberId("202011288")
        .password("password")
        .name("문다훈")
        .email("example@gmail.com")
        .batch("24-25")
        .build();
    given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

    // when
    subject.withdraw(targetId);

    // then
    assertTrue(member.isMemberDeleted());
    assertNotNull(member.getSoftDeletedAt());
  }

  @Test
  @DisplayName("withdraw : 존재하지 않는 멤버 탈퇴 실패")
  void should_fail_when_user_not_exists() {
    // given
    Long targetId = 1L;
    given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());

    // when
    Executable action = () -> subject.withdraw(targetId);

    // then
    assertThrows(UserNotFoundException.class, action);
  }

  @Test
  @DisplayName("withdraw : 이미 삭제된 멤버 탈퇴 실패")
  void should_fail_when_user_already_deleted() {
    // given
    Long targetId = 1L;
    Member member =
      Member.builder()
        .id(1L)
        .memberId("202011288")
        .password("password")
        .name("문다훈")
        .email("example@gmail.com")
        .batch("24-25")
        .build();
    given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

    // when `Member` soft deleted
    subject.withdraw(targetId);
    Executable action = () -> subject.withdraw(targetId);

    // then
    assertThrows(UserAlreadyDeletedException.class, action);
  }
}
