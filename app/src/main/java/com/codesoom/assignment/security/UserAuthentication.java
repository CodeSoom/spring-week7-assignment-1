package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 유저 인증 토큰.
 * 유저 인증 여부를 확인하기 위해 사용되는 객체.
 */
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
        return "UserAuthentication{" +
                "userId=" + userId +
                '}';
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // userId에 따른 다른 권한 부여 가능
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }
}
