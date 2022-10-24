package com.codesoom.assignment.application;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;

@Generated
public class UserCommand {

    @Generated
    @Getter
    @Builder
    public static class Register {
        private final String name;

        private final String password;

        private final String email;

    }

    @Generated
    @Getter
    @Builder
    public static class Update {
        private final Long id;

        private final String name;

        private final String password;
    }
}
