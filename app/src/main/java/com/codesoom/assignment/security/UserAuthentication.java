package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 로그인한 회원의 인증 정보를 저장합니다.
 */
public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
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

    /**
     * 접근하려는 리소스가 회원의 소유인지 확인합니다.
     * @param id
     * @return 리소스를 소유한 회원이면 true, 아니면 false
     */
    public boolean isSameUser(Long id) {
        Assert.notNull(id, "회원 정보는 필수값입니다.");
        return id.equals(getPrincipal());
    }
}
