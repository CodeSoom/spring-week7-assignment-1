package com.codesoom.assignment.securities;

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
        UserAuthentication anonymousAuthentication = (UserAuthentication) authentication;

        String accessToken = anonymousAuthentication.getAccessToken();

        Long userId = authenticationService.parseToken(accessToken);

        UserAuthentication auth = new UserAuthentication(Roles.USER, userId, accessToken);

        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthentication.class);
    }
}
