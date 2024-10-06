package gdsc.konkuk.platformcore.global.configs;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.*;
import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  private final GoogleOidcConfig googleOidcConfig;

  @Bean
  @Order(2)
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors->cors.configurationSource(corsConfigurationSource()))
        .securityMatcher(
            apiPath("/members/**"),
            apiPath("/events/**"),
            apiPath("/attendances/**"),
            apiPath("/emails/**"),
            apiPath("/admin/**"),
            "/docs/**",
            "/login")
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/docs/**")
                    .permitAll()
                    .requestMatchers("/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, apiPath("/members/{member-id}/password"))
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, apiPath("/members/check-login"))
                    .authenticated()
                    .anyRequest()
                    .hasRole("ADMIN"))
        .exceptionHandling(
            exception ->
                exception.authenticationEntryPoint(
                    (request, response, authException) ->
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
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
  @Order(1)
  public SecurityFilterChain googleOidcFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .securityMatcher(
            apiPath("/attendances/attend/**"),
            "/oauth2/authorization/google",
            "/login/oauth2/code/google")
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

  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://stage.gdsc-konkuk.dev", "https://gdsc-konkuk.dev", "https://admin.gdsc-konkuk.dev", "https://member.gdsc-konkuk.dev", "https://landing.gdsc-konkuk.dev"));
    configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization", "Location", "Range", "Cache-Control", "User-Agent", "DNT"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private ClientRegistration googleClientRegistration() {
    return ClientRegistration.withRegistrationId("google")
      .clientId(googleOidcConfig.getClientId())
      .clientSecret(googleOidcConfig.getClientSecret())
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .redirectUri("https://{baseHost}{basePort}{basePath}/login/oauth2/code/{registrationId}")
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
