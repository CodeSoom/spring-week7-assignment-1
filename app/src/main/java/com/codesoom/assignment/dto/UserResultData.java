package com.codesoom.assignment.dto;

import lombok.Builder;
import lombok.Getter;

public class UserResultData {
    @Getter
    private Long id;

    @Getter
    private String email;

    @Getter
    private String name;

    public UserResultData() {}

    @Builder
    public UserResultData(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
