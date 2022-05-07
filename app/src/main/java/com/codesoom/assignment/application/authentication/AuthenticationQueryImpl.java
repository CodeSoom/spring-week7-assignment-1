package com.codesoom.assignment.application.authentication;


import org.springframework.stereotype.Component;


import com.codesoom.assignment.domain.auth.AuthenticationQuery;

@Component
public class AuthenticationQueryImpl implements AuthenticationQuery {
    @Override
    public boolean isAccessible(String token) {
        return false;
    }
}
