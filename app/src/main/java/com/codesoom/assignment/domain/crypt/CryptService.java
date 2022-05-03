package com.codesoom.assignment.domain.crypt;

public interface CryptService {
    String encode(String password);
    boolean isMatch(String inputPassword, String savePassword);
}
