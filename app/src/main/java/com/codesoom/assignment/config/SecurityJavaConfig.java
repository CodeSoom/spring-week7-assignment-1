package com.codesoom.assignment.config;

import javax.servlet.Filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.AuthenticationErrorFilter;
import com.codesoom.assignment.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

	private final AuthenticationService authenticationService;

	public SecurityJavaConfig(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);

		Filter authenticationErrorFilter = new AuthenticationErrorFilter();
		http.csrf().disable()
			.addFilter(authenticationFilter)
			.addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
	}
}
