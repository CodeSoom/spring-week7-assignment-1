package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.UserType;
import lombok.Getter;

@Getter
public class ClaimData {
    private Long userId;
    private UserType userType;

    public ClaimData(Long userId, UserType userType) {
        this.userId = userId;
        this.userType = userType;
    }
}
