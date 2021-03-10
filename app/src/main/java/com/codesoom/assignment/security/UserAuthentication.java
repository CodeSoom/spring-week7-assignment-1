package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
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
        return "Authentication{" + userId + "}";
    }

    private static List<GrantedAuthority> authorities() {
        return null;
    }
}
