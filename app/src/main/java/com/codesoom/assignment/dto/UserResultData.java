package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.User;
import com.github.dozermapper.core.Mapping;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public class UserResultData {
    @Mapping("id")
    private Long id;

    @Mapping("name")
    private String name;

    @Mapping("email")
    private String email;

    @Mapping("password")
    private String password;

    @Mapping("deleted")
    private boolean deleted;

    @Builder
    public UserResultData(Long id, String name, String email, String password, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.deleted = deleted;
    }

    public User toEntity() {
        return User.builder()
            .id(this.id)
            .name(this.name)
            .email(this.email)
            .password(this.password)
            .deleted(this.deleted)
            .build();
    }

    public static UserResultData from(Long id, String name, String email) {
        return UserResultData.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static UserResultData of(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .deleted(user.isDeleted())
                .build();
    }
}
