package com.codesoom.assignment.application;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Generated
public class SessionCommand {

    @Generated
    @Getter
    @Builder
    @ToString
    public static class SessionRequest {
        private final String email;

        private final String password;

    }
}

