package com.codesoom.assignment.config;
import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.AuthenticationErrorFilter;
import com.codesoom.assignment.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

/**
 * 스프링 시큐리티 보안 설정
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final AuthenticationService authenticationService;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/h2-console/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
		Filter authenticationErrorFilter = new AuthenticationErrorFilter();
		http
			.csrf().disable()
			.addFilter(authenticationFilter)
			.addFilterBefore(authenticationErrorFilter,
				JwtAuthenticationFilter.class)
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(
				new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
			);

	}
}