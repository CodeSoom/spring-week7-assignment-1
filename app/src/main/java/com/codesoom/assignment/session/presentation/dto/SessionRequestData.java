package com.codesoom.assignment.session.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionRequestData {
    private String email;
    private String password;

    @Builder
    public SessionRequestData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
