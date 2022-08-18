package com.codesoom.assignment.security;

import com.codesoom.assignment.domain.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;
    private final Role role;

    public UserAuthentication(Long userId, Role role) {
        super(authorities(role));
        this.role = role;
        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
