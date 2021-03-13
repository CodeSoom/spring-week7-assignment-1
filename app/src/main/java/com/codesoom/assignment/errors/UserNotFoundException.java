package com.codesoom.assignment.errors;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User ID not found: " + id);
    }
}
