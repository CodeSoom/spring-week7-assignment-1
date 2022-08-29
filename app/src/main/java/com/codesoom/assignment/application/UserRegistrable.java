package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserRegistrationData;

/**
 * 사용자 정보를 등록합니다.
 */
public interface UserRegistrable {
    User registerUser(UserRegistrationData registrationData);
}
