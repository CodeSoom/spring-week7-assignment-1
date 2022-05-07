package com.codesoom.assignment.application.authentication;

import com.codesoom.assignment.domain.auth.AuthenticationCommandValidator;
import com.codesoom.assignment.domain.crypt.CryptService;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserQuery;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationCommandValidatorImpl implements AuthenticationCommandValidator {

    private final UserQuery userQuery;
    private final CryptService cryptService;

    public AuthenticationCommandValidatorImpl(
            UserQuery userQuery,
            CryptService cryptService
    ) {
        this.userQuery = userQuery;
        this.cryptService = cryptService;
    }


    @Override
    public void loginValidator(User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        user = userQuery.findByEmail(email);
        user.authenticate(cryptService, password);

    }


}
