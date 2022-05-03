package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.crypt.CryptService;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserModifier;
import com.codesoom.assignment.domain.user.UserModifierValidator;
import com.codesoom.assignment.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class UserModifierImpl implements UserModifier {
    private UserRepository userRepository;
    private UserModifierValidator userModifierValidator;
    private CryptService cryptService;

    @Autowired
    public UserModifierImpl(
            UserRepository userRepository,
            UserModifierValidator userModifierValidator,
            CryptService cryptService
    ) {
        this.userRepository = userRepository;
        this.userModifierValidator = userModifierValidator;
        this.cryptService = cryptService;
    }

    public UserModifierImpl(
            UserModifierValidator userModifierValidator,
            CryptService cryptService
    ) {
        this.userModifierValidator = userModifierValidator;
        this.cryptService = cryptService;
    }

    @Override
    public void leave(long userId) {

    }

    @Override
    public void modify(User user) {

    }

    @Override
    public void join(User user) {
        userModifierValidator.joinValidator(user);
        user = user.passwordEncode(cryptService);
        userRepository.save(user);
    }
}
