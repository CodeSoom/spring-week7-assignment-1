package com.codesoom.assignment.config;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.AuthenticationErrorFilter;
import com.codesoom.assignment.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
	private static String BASE_URL = "localhost:8080";
	private final AuthenticationService authenticationService;

	public SecurityJavaConfig(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
		Filter authenticationErrorFilter = new AuthenticationErrorFilter();
		http
			.cors()
			.and()
			.csrf()
			.disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilter(authenticationFilter)
			.addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
