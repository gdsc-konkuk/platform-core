package gdsc.konkuk.platformcore.application.member;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.global.exceptions.ErrorCode;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	private boolean checkMemberAlreadyExist(String memberId) {
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		return member.isPresent();
	}

	@Transactional
	public SuccessResponse register(MemberRegisterRequest registerRequest) {

		if(checkMemberAlreadyExist(registerRequest.getMemberId())) {
			throw UserAlreadyExistException.of(ErrorCode.USER_ALREADY_EXISTS);
		}

		Member member = Member.builder()
			.memberId(registerRequest.getMemberId())
			.password(passwordEncoder.encode(registerRequest.getPassword()))
			.name(registerRequest.getName())
			.email(registerRequest.getEmail())
			.role(registerRequest.getMemberRole())
			.batch(registerRequest.getBatch())
			.build();

		memberRepository.save(member);

		return SuccessResponse.messageOnly();
	}
}
