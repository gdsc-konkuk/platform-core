package gdsc.konkuk.platformcore.global.configs;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.*;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationFailureHandler;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  private final GoogleOidcConfig googleOidcConfig;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
      // TODO: csrf, dev에서만 disable
      .csrf(AbstractHttpConfigurer::disable)
      .securityMatcher(apiPath("/members/**"), apiPath("/admin/**"), "/docs/**", "/login")
      .authorizeHttpRequests(
        authorize ->
          authorize
            .requestMatchers("/docs/**")
            .permitAll()
            // TODO: member register, dev에서만 permitAll
            .requestMatchers(HttpMethod.POST, apiPath("/members"))
            .permitAll()
            .requestMatchers(apiPath("/admin/**"))
            .hasRole("ADMIN")
            .anyRequest()
            .authenticated())
      .formLogin(
        login ->
          login
            .defaultSuccessUrl("/")
            .usernameParameter(LOGIN_NAME)
            .successHandler(customAuthenticationSuccessHandler)
            .failureHandler(customAuthenticationFailureHandler));
    return httpSecurity.build();
  }

  @Bean
  public SecurityFilterChain googleOidcFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
      // TODO: dev에서만 disable
      .csrf(AbstractHttpConfigurer::disable)
      .securityMatcher(
        apiPath("/attendances/**"), "/oauth2/authorization/google", "/login/oauth2/code/google")
      .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
      .oauth2Login(withDefaults());
    return httpSecurity.build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
  }

  private ClientRegistration googleClientRegistration() {
    return ClientRegistration.withRegistrationId("google")
      .clientId(googleOidcConfig.getClientId())
      .clientSecret(googleOidcConfig.getClientSecret())
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
      .scope("openid", "profile", "email")
      .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
      .tokenUri("https://www.googleapis.com/oauth2/v4/token")
      .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
      .userNameAttributeName(IdTokenClaimNames.SUB)
      .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
      .clientName("Google")
      .build();
  }
}
