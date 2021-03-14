package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities() {
        return new ArrayList<>(
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String toString() {
        return "UserAuthentication(" + userId + ")";
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
