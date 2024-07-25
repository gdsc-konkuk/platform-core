package gdsc.konkuk.platformcore.global.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("spring.security.oauth2.client.registration.google")
public class GoogleOidcConfig {
  private final String clientId;
  private final String clientSecret;
}
