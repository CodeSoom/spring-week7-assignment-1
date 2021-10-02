package com.codesoom.assignment.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    /**
     * Creates a token with the supplied array of authorities.
     */
    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    private static List<? extends GrantedAuthority> authorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + userId;
    }
}
