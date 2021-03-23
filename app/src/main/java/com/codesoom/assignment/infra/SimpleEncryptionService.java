package com.codesoom.assignment.infra;

import com.codesoom.assignment.domain.EncryptionService;
import org.springframework.stereotype.Component;

@Component
public class SimpleEncryptionService implements EncryptionService {
    @Override
    public String encryptedValue(String aPlainTextValue) {
        return aPlainTextValue + "LAS_SALT";
    }
}
