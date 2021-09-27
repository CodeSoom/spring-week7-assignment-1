package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.suppliers.EntitySupplier;
import com.github.dozermapper.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModificationData implements EntitySupplier<User> {
    @NotBlank
    @Mapping("name")
    private String name;

    @NotBlank
    @Size(min = 4, max = 1024)
    @Mapping("password")
    private String password;

    @Override
    public User toEntity() {
        return User.builder()
                .name(name)
                .password(password)
                .build();
    }
}
