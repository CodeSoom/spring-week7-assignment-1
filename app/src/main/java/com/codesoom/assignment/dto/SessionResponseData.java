package com.codesoom.assignment.dto;

import lombok.Builder;
import lombok.Getter;

public class SessionResponseData {
    @Getter
    private String accessToken;

    public SessionResponseData() {}

    @Builder
    public SessionResponseData(String accessToken) {
        this.accessToken = accessToken;
    }
}
