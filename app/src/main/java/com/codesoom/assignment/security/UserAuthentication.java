package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final String email;

    public UserAuthentication(String email) {
        super(authorities(email));
        this.email = email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "email='" + email + '\'' +
                '}';
    }

    private static List<GrantedAuthority> authorities(String email) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        if (email.equals("adminEmail")) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        return authorities;
    }
}
