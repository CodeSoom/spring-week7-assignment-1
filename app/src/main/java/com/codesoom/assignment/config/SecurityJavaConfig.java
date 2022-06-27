package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filters.AuthenticationErrorFilter;
import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http.csrf().disable()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
        // super.configure(http);
    }

}
