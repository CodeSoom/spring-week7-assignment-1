package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 인증 정보를 구현한다
 */
public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    /**
     * 사용자 권한을 설정하고 반환한다
     *
     * @return 사용자 권한 목록
     */
    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER")); // enum USER ADMIN GUEST
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 사용자 아이디를 반환한다
     *
     * @return 사용자 아이디
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }
}
