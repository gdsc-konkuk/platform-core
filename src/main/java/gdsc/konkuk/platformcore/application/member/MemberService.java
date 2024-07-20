package gdsc.konkuk.platformcore.application.member;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.controller.member.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional
	public SuccessResponse register(MemberRegisterRequest registerRequest) {

		if(checkMemberAlreadyExist(registerRequest.getMemberId())) {
			throw UserAlreadyExistException.of(MemberErrorCode.USER_ALREADY_EXISTS);
		}

		String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
		registerRequest.setPassword(encodedPassword);
		memberRepository.save(MemberRegisterRequest.toEntity(registerRequest));

		return SuccessResponse.messageOnly();
	}

	private boolean checkMemberAlreadyExist(String memberId) {
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		return member.isPresent();
	}
}
