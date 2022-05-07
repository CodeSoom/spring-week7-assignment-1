package com.codesoom.assignment.dto.auth;

import com.codesoom.assignment.common.suppliers.EntitySupplier;
import com.codesoom.assignment.common.vaildator.Email;
import com.codesoom.assignment.common.vaildator.Password;
import com.codesoom.assignment.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import static com.codesoom.assignment.domain.user.User.UserBuilder;

@Getter
@Builder
public class AuthRequestData implements EntitySupplier<User> {
    @Email
    private String email;
    @Password
    private String password;


    @Override
    public User toEntity() {
        return UserBuilder.builder()
                .email(email)
                .password(password)
                .build();
    }
}
