package gdsc.konkuk.platformcore.annotation;

import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomUserSecurityContextFactory.class)
public @interface WithCustomUser {
  String memberId() default "2024000000";
  MemberRole role() default MemberRole.MEMBER;
}
