package com.codesoom.assignment.domain.user;

public interface UserModifierValidator {
    void leaveValidator(User user);
    void modifyValidator(User user);
    void joinValidator(User user);
}
