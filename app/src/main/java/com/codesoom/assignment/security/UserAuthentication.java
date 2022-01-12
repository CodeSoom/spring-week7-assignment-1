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

    /**
     * principal(주체)의 credential(증명서)를 반환합니다.
     *
     * @return Object principal(주체)의 증명서
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 접근 principal(주체)를 반환합니다.
     *
     * @return Object 접근 principal(주체)
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    /**
     * 인증 여부를 반환합니다.
     *
     * @return boolean 인증 여부
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String toString() {
        return "UserAuthentication(" + userId + "}";
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        return authorities;
    }
}
