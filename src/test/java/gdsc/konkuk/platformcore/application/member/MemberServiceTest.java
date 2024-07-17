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
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;

class MemberServiceTest {

	private MemberService subject;

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
	@DisplayName("새로운 멤버 회원가입 성공")
	@Transactional
	void should_success_when_newMember_register() {
	    //given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.memberRole(MemberRole.MEMBER)
				.batch(2024)
				.build();
	    given(memberRepository.findByMemberId(any())).willReturn(Optional.empty());
		given(passwordEncoder.encode(any())).willReturn("password");
	    //when
		SuccessResponse expected = SuccessResponse.messageOnly();
		SuccessResponse actual = subject.register(memberRegisterRequest);
	    //then
		assertEquals(expected.isSuccess(), actual.isSuccess());
		assertEquals(expected.getData(), actual.getData());
	}

	@Test
	@DisplayName("이미 존재하는 멤버 회원가입 실패")
	@Transactional
	void should_fail_when_already_exist_member_register() {
		//given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.memberRole(MemberRole.MEMBER)
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
}