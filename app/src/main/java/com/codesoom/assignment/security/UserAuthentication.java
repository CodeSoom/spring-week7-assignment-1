package com.codesoom.assignment.security;

import com.codesoom.assignment.domain.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(Long userId, List<Role> roles) {
        super(authorities(userId, roles));
        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities(
            Long userId, List<Role> roles
    ) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
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
    public String toString() {
        return "Authentication{" + "userId=" + userId + '}';
    }

    public Long getUserId() {
        return userId;
    }
}
