package com.codesoom.assignment.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegistrationData {
    @NotBlank
    @Size(min = 3)
    private final String email;

    @NotBlank
    private final String name;

    @NotBlank
    @Size(min = 4, max = 1024)
    private final String password;

    public UserRegistrationData(
        String email,
        String name,
        String password
    ) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
