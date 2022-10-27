package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.validator.SameConsecutiveChar;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Generated
public class UserDto {

    @Generated
    @Getter
    @Setter
    public static class RegisterParam {
        @NotBlank
        @Size(min = 3)
        private String email;

        @NotBlank
        private String name;

        @NotBlank
        @Size(min = 13, max = 1024)
        @SameConsecutiveChar
        private String password;
    }

    @Generated
    @Getter
    @Setter
    public static class UpdateParam {
        @NotBlank
        private String name;

        @NotBlank
        @Size(min = 13, max = 1024)
        @SameConsecutiveChar
        private String password;

    }

    @Generated
    @Getter
    @ToString
    public static class UserInfo {
        private final Long id;

        private final String email;

        private final String name;

        public UserInfo(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
        }
    }
}
