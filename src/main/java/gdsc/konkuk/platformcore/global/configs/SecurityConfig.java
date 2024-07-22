package gdsc.konkuk.platformcore.global.configs;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationFailureHandler;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.csrf(csrf -> csrf.ignoringRequestMatchers(
					new AntPathRequestMatcher("/login", "POST"),
					new AntPathRequestMatcher("/api/v1/members", "POST")
				)
			)
			.addFilterBefore(new SecurityContextPersistenceFilter(), UsernamePasswordAuthenticationFilter.class)

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(apiPath("/docs/**")).permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/members").permitAll()
				.requestMatchers(apiPath("/admin/**")).hasRole("ADMIN")
				.anyRequest().permitAll())

			.formLogin(login -> login
				.defaultSuccessUrl("/")
				.usernameParameter(LOGIN_NAME)
				.successHandler(customAuthenticationSuccessHandler)
				.failureHandler(customAuthenticationFailureHandler)
			);
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
