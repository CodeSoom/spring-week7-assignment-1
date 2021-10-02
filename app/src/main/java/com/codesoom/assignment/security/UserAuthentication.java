package com.codesoom.assignment.security;

import com.github.dozermapper.core.Mapping;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//public class UserAuthentication implements Authentication { 너무 많은 것을 구현해 주어야 한다. 아래를 사용하자.
public class UserAuthentication extends AbstractAuthenticationToken {
    @Mapping("id")
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities(userId));
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
        //return super.isAuthenticated();
    }

    private static List<GrantedAuthority> authorities(Long userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Todo 도메인 설계 필요
        if(userId == 1L){
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }else{
            authorities.add(new SimpleGrantedAuthority("USER"));
        }
        return authorities;
    }
}
