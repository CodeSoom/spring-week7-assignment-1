package com.codesoom.assignment.domain;

import org.springframework.stereotype.Service;

@Service
public interface EncryptionService {
    String encryptedValue(String aPlainTextValue);
}
