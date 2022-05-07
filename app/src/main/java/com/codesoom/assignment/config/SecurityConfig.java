package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.JwtAuthenticationFilter;
import com.codesoom.assignment.filter.JwtExceptionTranslationFilter;
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

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(authenticationManager(), authenticationService);

        JwtExceptionTranslationFilter jwtExceptionTranslationFilter = new JwtExceptionTranslationFilter();

        http.addFilter(jwtAuthenticationFilter);
        http.addFilterBefore(jwtExceptionTranslationFilter, JwtAuthenticationFilter.class);
    }
}
