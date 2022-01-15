package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities(userId));
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
     * 인증이 되었으면 true를 반환하고, 인증이 안되었으면 false를 반환합니다.
     *
     * @return boolean 인증이 되면 true, 아니면 false
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String toString() {
        return "UserAuthentication(" + userId + "}";
    }

    private static List<GrantedAuthority> authorities(Long userId) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        if(userId == 1004) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        return authorities;
    }

    public Long getUserId() {
        return userId;
    }
}
