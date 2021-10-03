package com.codesoom.assignment.securities;

import lombok.Getter;

/**
 * 회원 권한을 정의합니다.
 */
public enum Roles {
    ANONYMOUS("ANONYMOUS"),
    ADMIN("ADMIN"),
    USER("USER");

    @Getter
    private final String roleCode;

    Roles(String roleCode) {
        this.roleCode = roleCode;
    }
}
