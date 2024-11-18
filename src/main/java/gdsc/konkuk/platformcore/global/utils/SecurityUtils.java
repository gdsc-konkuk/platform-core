package gdsc.konkuk.platformcore.global.utils;

import java.util.Map;
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
      Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
      String memberId = principal.get("memberId").toString();
      return Long.valueOf(memberId);
    }

    throw new IllegalStateException("비정상적인 인증정보입니다.");
  }
}
