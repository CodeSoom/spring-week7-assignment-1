package com.codesoom.assignment.security;

/**
 * 회원 권한을 정의합니다.
 */
public enum Roles {
    ANONYMOUS("ANONYMOUS"),
    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
