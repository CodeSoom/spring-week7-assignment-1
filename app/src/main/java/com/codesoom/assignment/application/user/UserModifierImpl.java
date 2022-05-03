package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserModifier;
import com.codesoom.assignment.domain.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserModifierImpl implements UserModifier {
    public final UserRepository userRepository;

    public UserModifierImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void leave(long userId) {

    }

    @Override
    public void modify(User user) {

    }

    @Override
    public String join(User user) {
        return null;
    }
}
