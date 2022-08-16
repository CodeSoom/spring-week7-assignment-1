package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.infra.InMemoryUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserControllerTestConfig {
    private static final String SECRET = "12345678901234567890123456789012";

    @Bean
    AuthenticationService authenticationService() {
        UserRepository userRepository = new InMemoryUserRepository();
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        return new AuthenticationService(userRepository, jwtUtil);
    }
}
