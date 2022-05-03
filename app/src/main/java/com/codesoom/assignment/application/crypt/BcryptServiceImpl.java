package com.codesoom.assignment.application.crypt;

import com.codesoom.assignment.domain.crypt.CryptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptServiceImpl implements CryptService {

    private final PasswordEncoder passwordEncoder;

    public BcryptServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean isMatch(String inputPassword, String savePassword) {
        return passwordEncoder.matches(inputPassword, savePassword);
    }
}
