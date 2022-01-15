package com.codesoom.assignment.security;

import com.codesoom.assignment.domain.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public UserAuthentication(Long userId, List<Role> roles) {
        super(authorities(roles));
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

    /**
     * User의 id를 반환합니다.
     *
     * @return Long User의 id
     */
    public Long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserAuthentication(" + userId + "}";
    }

    private static List<GrantedAuthority> authorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
