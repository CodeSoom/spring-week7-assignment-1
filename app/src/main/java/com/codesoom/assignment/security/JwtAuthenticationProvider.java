package com.codesoom.assignment.security;

import com.codesoom.assignment.application.AuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAuthentication userAuthentication = (UserAuthentication) authentication;

        String acessToken = userAuthentication.getAccessToken();
        Long userId = authenticationService.parseToken(acessToken);

        return new UserAuthentication(Roles.USER, userId, acessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthentication.class);
    }
}
