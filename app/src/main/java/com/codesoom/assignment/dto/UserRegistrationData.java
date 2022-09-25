package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class UserRegistrationData {
    @NotBlank
    @Size(min = 3)
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 4, max = 1024)
    private String password;

    private UserRegistrationData() {
    }

    public User toUser(){
        return User.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .build();
    }
}
