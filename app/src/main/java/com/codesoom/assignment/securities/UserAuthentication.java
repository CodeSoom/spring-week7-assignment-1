package com.codesoom.assignment.securities;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 인증정보를 표현합니다.
 */
public class UserAuthentication extends AbstractAuthenticationToken {
    @Getter
    private String accessToken;

    private Long userId;

    public UserAuthentication(Roles role, Long userId, String accessToken) {
        super(authorities(role));
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public UserAuthentication(Roles role, String accessToken) {
        super(authorities(role));
        this.accessToken = accessToken;
    }

    /**
     * 회원 인증 회원 인증 키를 리턴합니다.
     * @return 토큰
     */
    @Override
    public Object getCredentials() {
        return accessToken;
    }

    /**
     * 회원 식별 정보를 리턴합니다.
     * @return 회원 식별자
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    /**
     * 회원 권한을 설정하고 리턴합니다.
     * @param role 회원 권한
     * @return 권한 목록
     */
    private static List<GrantedAuthority> authorities(Roles role) {
        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(role.getRoleCode()));

        return auth;
    }
}
