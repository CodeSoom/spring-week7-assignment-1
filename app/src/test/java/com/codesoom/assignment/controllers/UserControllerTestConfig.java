package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.infra.InMemoryUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserControllerTestConfig {
    private static final String SECRET = "12345678901234567890123456789012";

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    AuthenticationService authenticationService() {
        UserRepository userRepository = new InMemoryUserRepository();
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        return new AuthenticationService(userRepository, jwtUtil, passwordEncoder);
    }
}
