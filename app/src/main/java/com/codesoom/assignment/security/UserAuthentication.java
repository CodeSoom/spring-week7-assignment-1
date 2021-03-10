package com.codesoom.assignment.security;

import com.codesoom.assignment.dto.AuthenticationResultData;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final AuthenticationResultData authenticationResultData;

    public UserAuthentication(AuthenticationResultData authenticationResultData) {
        super(authorities());
        this.authenticationResultData = authenticationResultData;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authenticationResultData.getEmail();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "authenticationResultData=" + authenticationResultData +
                '}';
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }
}
