package com.codesoom.assignment.security;

import lombok.Generated;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    @Generated
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    private static List<? extends GrantedAuthority> authorities() {
        return List.of(
                new SimpleGrantedAuthority("USER")
        );
    }
}
