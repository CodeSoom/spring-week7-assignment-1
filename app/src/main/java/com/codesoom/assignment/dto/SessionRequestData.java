package com.codesoom.assignment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionRequestData {
    private String email;
    private String password;
}
