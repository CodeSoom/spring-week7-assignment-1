package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filter.BeforeAuthentication;
import com.codesoom.assignment.filter.JwtAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationService authenticationService;

    public SecurityJavaConfig() {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter jwtAuthentication = new JwtAuthentication(authenticationManager(), authenticationService);
        Filter beforeAuthentication = new BeforeAuthentication();
        http.csrf()
                .disable()
                .addFilter(jwtAuthentication)
                .addFilterBefore(beforeAuthentication, JwtAuthentication.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
