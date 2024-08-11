package gdsc.konkuk.platformcore.annotation;

import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMyCustomUserSecurityContextFactory.class)
public @interface CustomMockUser {
  String memberId() default "202011288";

  MemberRole role() default MemberRole.MEMBER;
}
