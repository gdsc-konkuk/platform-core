package gdsc.konkuk.platformcore.application.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gdsc.konkuk.platformcore.application.auth.exceptions.InvalidUserInfoException;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final MemberValidator memberValidator;

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

		Member member = memberRepository.findByMemberId(memberId)
			.orElseThrow(()-> InvalidUserInfoException.of(ErrorCode.USER_NOT_FOUND));

		memberValidator.validate(member);

		return new CustomUserDetails(member);

	}
}
