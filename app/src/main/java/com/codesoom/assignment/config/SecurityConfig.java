package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.JwtAuthorizationFilter;
import com.codesoom.assignment.filter.JwtAuthorizationExceptionTranslationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        JwtAuthorizationFilter jwtAuthorizationFilter =
                new JwtAuthorizationFilter(authenticationManager(), authenticationService);

        JwtAuthorizationExceptionTranslationFilter jwtAuthorizationExceptionTranslationFilter =
                new JwtAuthorizationExceptionTranslationFilter();

        http.addFilter(jwtAuthorizationFilter);
        http.addFilterBefore(jwtAuthorizationExceptionTranslationFilter, JwtAuthorizationFilter.class);
    }
}
