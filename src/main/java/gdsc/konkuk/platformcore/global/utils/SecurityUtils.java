package gdsc.konkuk.platformcore.global.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

	public static Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new IllegalStateException("인증 정보가 없습니다.");
		}

		if (authentication.isAuthenticated()) {
			return Long.valueOf(authentication.getName());
		}

		throw new IllegalStateException("비정상적인 인증정보입니다.");
	}
}
