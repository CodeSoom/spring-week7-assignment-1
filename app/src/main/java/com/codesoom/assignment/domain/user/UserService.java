package com.codesoom.assignment.domain.user;

import com.codesoom.assignment.dto.user.UserModificationData;
import com.codesoom.assignment.dto.user.UserRegistrationData;

public interface UserService {
    void join(UserRegistrationData registrationRequest);

    void modify(UserModificationData modificationRequest);

    void leave(Long userId);

    User findUser(Long id);

    User findUserByEmail(String email);
}
