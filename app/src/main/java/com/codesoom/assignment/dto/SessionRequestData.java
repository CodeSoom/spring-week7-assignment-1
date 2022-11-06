package com.codesoom.assignment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SessionRequestData {
    private String email;
    private String password;

    @JsonCreator
    public SessionRequestData(@JsonProperty String email, @JsonProperty String password) {
        this.email = email;
        this.password = password;
    }
}
