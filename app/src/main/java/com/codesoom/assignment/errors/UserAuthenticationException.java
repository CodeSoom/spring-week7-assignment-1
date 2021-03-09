package com.codesoom.assignment.errors;

public class UserAuthenticationException extends RuntimeException {

    public UserAuthenticationException(String cause) {
        super("Access Denied: " + cause);
    }
    
}
