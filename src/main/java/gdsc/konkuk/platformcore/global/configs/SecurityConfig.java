package gdsc.konkuk.platformcore.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.csrf(AbstractHttpConfigurer::disable)

			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			.addFilterBefore(new SecurityContextPersistenceFilter(), BasicAuthenticationFilter.class)

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/docs/**").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.requestMatchers("/member").hasRole("MEMBER")
				.anyRequest().authenticated())

			.formLogin(login -> login
				.defaultSuccessUrl("/")
				.usernameParameter("username")
				.passwordParameter("password")
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
}
