package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.interceptors.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);

        Filter authenticationErrorFilter = new AuthenticationErrorFilter();
        http.csrf()
                .disable()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
    }
}
