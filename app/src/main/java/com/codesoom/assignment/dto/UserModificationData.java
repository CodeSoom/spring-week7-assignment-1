package com.codesoom.assignment.dto;

import com.github.dozermapper.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 변경될 name 혹은 password
 * 혹은 둘 다 가지고 있을 수 있다.
 */
@Getter
public class UserModificationData {
    @Mapping("name")
    private String name;

    @Size(min = 4, max = 1024)
    @Mapping("password")
    private String password;

    @Builder
    public UserModificationData(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
