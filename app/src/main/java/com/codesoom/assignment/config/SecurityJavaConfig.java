package com.codesoom.assignment.config;

import com.codesoom.assignment.filters.AuthenticationErrorFilter;
import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    private final JwtUtil jwtUtil;
    public SecurityJavaConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtUtil);
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
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
