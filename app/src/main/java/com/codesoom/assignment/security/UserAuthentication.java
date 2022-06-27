package com.codesoom.assignment.security;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.User;
import lombok.Generated;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(User user) {
        super(authorities(user.getRoles()));
        this.userId = user.getId();
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

    private static List<? extends GrantedAuthority> authorities(List<Role> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
        return Arrays.asList(new SimpleGrantedAuthority("USER"));
    }
}
