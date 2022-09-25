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
public class UserModificationData {
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 4, max = 1024)
    private String password;

    private UserModificationData(){

    }

    public User toUser(){
        return User.builder()
                .name(name)
                .password(password)
                .build();
    }
}
