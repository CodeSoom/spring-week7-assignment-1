package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.PasswordSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyPasswordEncoder {
    private final PasswordEncoder passwordEncoder;

    public String getEncodedPassword(PasswordSupplier passwordSupplier) {
        return passwordEncoder.encode(passwordSupplier.getPassword());
    }
}
