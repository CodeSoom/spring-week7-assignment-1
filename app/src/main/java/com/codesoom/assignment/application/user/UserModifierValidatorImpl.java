package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserModifierValidator;
import com.codesoom.assignment.domain.user.UserRepository;
import com.codesoom.assignment.errors.user.UserEmailDuplicationException;
import org.springframework.stereotype.Component;

//TODO: 각 validation 마다 component로 구분 고려
@Component
public class UserModifierValidatorImpl implements UserModifierValidator {

    private final UserRepository userRepository;

    public UserModifierValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void leaveValidator(User user) {

    }

    @Override
    public void modifyValidator(User user) {

    }

    @Override
    public void joinValidator(User user) {
        String userEmail = user.getEmail();
        boolean isExistEmail = userRepository.existsByEmail(userEmail);
        if (isExistEmail) {
            throw new UserEmailDuplicationException(userEmail);
        }
    }
}
