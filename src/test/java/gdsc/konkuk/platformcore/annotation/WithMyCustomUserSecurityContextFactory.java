package gdsc.konkuk.platformcore.annotation;

import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMyCustomUserSecurityContextFactory implements WithSecurityContextFactory<CustomMockUser> {
  @Override
  public SecurityContext createSecurityContext(CustomMockUser annotation) {
    String memberId = annotation.memberId();
    MemberRole role = annotation.role();

    Authentication auth = new UsernamePasswordAuthenticationToken(
      memberId,
      "",
      buildGrantedAuthoritiesFromRole(role)
    );

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(auth);

    return context;
  }

  private List<GrantedAuthority> buildGrantedAuthoritiesFromRole(final MemberRole role) {
    return List.of(new SimpleGrantedAuthority(role.toString()));
  }
}