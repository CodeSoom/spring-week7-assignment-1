package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.User;
import lombok.Getter;

@Getter
public class UserInquiryInfo {
    private final Long id;
    private final String email;
    private final String name;
    private final Role role;

    public UserInquiryInfo(Long id, String email, String name, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static UserInquiryInfo from(User user) {
        return new UserInquiryInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}
