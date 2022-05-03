package com.codesoom.assignment.dto.user;

import com.codesoom.assignment.common.vaildator.Email;
import com.codesoom.assignment.common.vaildator.Name;
import com.codesoom.assignment.common.vaildator.Password;
import com.codesoom.assignment.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


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

    public User toEntity() {
        return User.UserBuilder
                .builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }
}
