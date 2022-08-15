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
        return "Authentication( " + userId + ")";
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        //TODO : userID에 따라서 다른 권한 부여 -> ADMIN, ETC...

        //authorities.add(new SimpleGrantedAuthority("USER"));

        return authorities;
    }

}
