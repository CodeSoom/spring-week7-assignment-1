package com.codesoom.assignment.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserModificationData {
    @NotBlank
    private final String name;

    @NotBlank
    @Size(min = 4, max = 1024)
    private final String password;

    public UserModificationData(@NotBlank String name, @NotBlank @Size(min = 4, max = 1024) String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
