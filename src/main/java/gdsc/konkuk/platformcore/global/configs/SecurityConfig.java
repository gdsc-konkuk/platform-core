package gdsc.konkuk.platformcore.global.configs;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.apiPath;

import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationFailureHandler;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationSuccessHandler;
import gdsc.konkuk.platformcore.application.auth.CustomOAuthUserService;
import gdsc.konkuk.platformcore.application.auth.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuthUserService customOAuthUserService;
    private final GoogleOidcConfig googleOidcConfig;
    private final CorsConfig corsConfig;

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                        cors.configurationSource(corsConfig.getConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        // 모두를 위한 API
                        .requestMatchers("/login/**", "/docs/**", "/actuator/**")
                        .permitAll()
                        // 동아리 운영진을 위한 API
                        .anyRequest()
                        .hasAnyRole("CORE", "LEAD"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN)))
                .oauth2Login(login -> login
                        .clientRegistrationRepository(new InMemoryClientRegistrationRepository(
                                googleOidcConfig.googleClientRegistration()))
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/login/oauth2/authorization"))
                        .redirectionEndpoint(redirection ->
                                redirection.baseUri("/login/oauth2/code/{registrationId}"))
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOAuthUserService))
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler));
        return httpSecurity.build();
    }

    @Bean
    @Order(1)
    // 출석을 위한 SecurityFilterChain
    public SecurityFilterChain googleOidcFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher(apiPath("/attendances/attend/**"), "/login/attendance/**")
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authorize -> authorize
                        // 모두를 위한 API
                        .requestMatchers("/login/attendance/**")
                        .permitAll()
                        // 동아리 회원을 위한 API
                        .anyRequest()
                        .hasAnyRole("CORE", "LEAD", "MEMBER"))
                .oauth2Login(login -> login
                        .clientRegistrationRepository(new InMemoryClientRegistrationRepository(
                                googleOidcConfig.googleAttendanceClientRegistration()))
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/login/attendance/authorization"))
                        .redirectionEndpoint(redirection ->
                                redirection.baseUri("/login/attendance/code/{registrationId}"))
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOAuthUserService)));
        return httpSecurity.build();
    }
}
