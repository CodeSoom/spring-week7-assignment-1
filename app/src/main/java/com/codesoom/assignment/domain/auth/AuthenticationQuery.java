package com.codesoom.assignment.domain.auth;

public interface AuthenticationQuery {
    boolean isAccessible(String token);
}
