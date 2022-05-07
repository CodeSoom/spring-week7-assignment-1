package com.codesoom.assignment.application.authentication;

import com.codesoom.assignment.domain.auth.AuthenticationCommand;
import com.codesoom.assignment.domain.auth.AuthenticationCommandValidator;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.security.jwt.JwtGenerator;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class AuthenticationCommandImpl implements AuthenticationCommand {

    private final AuthenticationCommandValidator authenticationCommandValidator;
    private final JwtGenerator jwtUtil;

    public AuthenticationCommandImpl(
            AuthenticationCommandValidator authenticationCommandValidator,
            JwtGenerator jwtUtil
    ) {
        this.authenticationCommandValidator = authenticationCommandValidator;
        this.jwtUtil=jwtUtil;
    }

    @Override
    public String login(User user) {
        String userEmail = user.getEmail();
        authenticationCommandValidator.loginValidator(user);

        return jwtUtil.generateToken(userEmail);
    }
}
