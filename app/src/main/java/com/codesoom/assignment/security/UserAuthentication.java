package com.codesoom.assignment.security;

import com.codesoom.assignment.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 인증된 사용자
 */
public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(User user) {
        super(user, null, authorities());
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }
}
