package gdsc.konkuk.platformcore.application.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
	private PasswordEncoder passwordEncoder;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		subject = new MemberService(passwordEncoder, memberRepository);
	}

	@Test
	@DisplayName("Register : 새로운 멤버 회원가입 성공")
	void should_success_when_newMember_register() {
		//given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.batch(2024)
				.build();
		given(memberRepository.findByMemberId(any())).willReturn(Optional.empty());
		given(memberRepository.save(any(Member.class))).willReturn(member);
		given(passwordEncoder.encode(any())).willReturn("password");
	    //when
		Member actual = subject.register(memberRegisterRequest);
	    //then
		assertNotNull(actual);
	}

	@Test
	@DisplayName("Register : 이미 존재하는 멤버 회원가입 실패")
	void should_fail_when_already_exist_member_register() {
		//given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.batch(2024)
				.build();
		given(memberRepository.findByMemberId(any()))
			.willReturn(Optional.of(MemberRegisterRequest
				.toEntity(memberRegisterRequest)));

		//then
		assertThrows(UserAlreadyExistException.class, () -> {
			//when
			subject.register(memberRegisterRequest);
		});
	}

	@Test
	@DisplayName("withdraw : 존재하는 멤버 탈퇴 성공")
	void should_success_when_user_exists() {
		// given
		Long targetId = 1L;
		Member member = Member.builder()
			.id(1L)
			.memberId("202011288")
			.password("password")
			.name("문다훈")
			.email("example@gmail.com")
			.batch(2024)
			.build();
		// when
		when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(member));
		subject.withdraw(targetId);
		//then
		assertTrue(member.isMemberDeleted());
		assertNotNull(member.getSoftDeletedAt());
	}

	@Test
	@DisplayName("withdraw : 존재하지 않는 멤버 탈퇴 실패")
	void should_fail_when_user_not_exists() {
		// given
		Long targetId = 1L;
		when(memberRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		// then
		assertThrows(UserNotFoundException.class, () -> {
			// when
			subject.withdraw(targetId);
		});
	}

	@Test
	@DisplayName("withdraw : 이미 삭제된 멤버 탈퇴 실패")
	void should_fail_when_user_already_deleted() {
		// given
		Long targetId = 1L;
		Member member = Member.builder()
			.id(1L)
			.memberId("202011288")
			.password("password")
			.name("문다훈")
			.email("example@gmail.com")
			.batch(2024)
			.build();
		// when
		when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(member));
		// Member soft deleted.
		subject.withdraw(targetId);

		// then
		assertThrows(UserAlreadyDeletedException.class, () -> {
			// when
			subject.withdraw(targetId);
		});
	}
}