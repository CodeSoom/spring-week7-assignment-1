package com.codesoom.assignment.dto.user;

import com.codesoom.assignment.common.vaildator.Email;
import com.codesoom.assignment.common.vaildator.Name;
import com.codesoom.assignment.common.vaildator.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class UserRegistrationData {

    @Email
    private String email;

    @Name
    private String name;

    @Password
    private String password;
}
