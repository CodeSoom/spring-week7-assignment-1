package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private static List<? extends GrantedAuthority> authorities() {
        return null;
    }

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
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
