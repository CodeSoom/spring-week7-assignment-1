package com.codesoom.assignment.dto;

import lombok.Builder;
import lombok.Getter;

public class SessionRequestData {
    @Getter
    private String email;

    @Getter
    private String password;

    @Builder
    public SessionRequestData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
