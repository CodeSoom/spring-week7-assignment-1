package com.codesoom.assignment.security;

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
     * 사용자 인증 키를 리턴합니다.
     *
     * @return 토큰
     */
    @Override
    public Object getCredentials() {
        return accessToken;
    }

    /**
     * 사용자 아이디를 리턴합니다.
     *
     * @return 사용자 아이디
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    /**
     * 사용자 권한을 설정하고 리턴합니다.
     *
     * @param role 회원 권한
     * @return 권한 목록
     */
    private static List<GrantedAuthority> authorities(Roles role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(role.getRole()));
        return authorities;
    }
}
