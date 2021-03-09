package com.codesoom.assignment.config;

import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.servlet.Filter;

@Configuration
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Filter authenticationFilter = new JwtAuthenticationFilter(
                authenticationManager());

        // CSRF(Cross-Site Request Forgery) 비활성화
        http
                .csrf().disable()
                .addFilter(authenticationFilter);

    }
}
