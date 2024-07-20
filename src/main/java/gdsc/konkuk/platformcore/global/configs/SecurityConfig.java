package gdsc.konkuk.platformcore.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationFailureHandler;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private static final String API_PREFIX = "/api/v1";
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.addFilterBefore(new SecurityContextPersistenceFilter(), BasicAuthenticationFilter.class)

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(apiPath("/docs/**")).permitAll()
				.requestMatchers(HttpMethod.POST, apiPath("/members")).permitAll()
				.requestMatchers(apiPath("/admin/**")).hasRole("ADMIN")
				.anyRequest().authenticated())

			.formLogin(login -> login
				.defaultSuccessUrl("/")
				.successHandler(customAuthenticationSuccessHandler)
				.failureHandler(customAuthenticationFailureHandler)
				.permitAll()
			);
		return httpSecurity.build();
	}

	@Bean
	public SecurityFilterChain swaggerFilterchain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.securityMatcher("/docs")
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/**").authenticated());
		return httpSecurity.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private String apiPath(String path) {
		return API_PREFIX + path;
	}
}
