package com.codesoom.assignment.security;

import lombok.Getter;

/**
 * 회원 권한을 정의합니다.
 */
public enum Roles {
    ANONYMOUS("ANONYMOUS"),
    ADMIN("ADMIN"),
    USER("USER");
    @Getter
    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
