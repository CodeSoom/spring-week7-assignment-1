package com.codesoom.assignment.dto;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Generated
public class SessionDto {
    @Generated
    @Getter
    @Setter
    public static class SessionRequestData {
        private String email;
        private String password;
    }

    @Generated
    @Getter
    @ToString
    public static class SessionInfo {
        private final String accessToken;

        public SessionInfo(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
