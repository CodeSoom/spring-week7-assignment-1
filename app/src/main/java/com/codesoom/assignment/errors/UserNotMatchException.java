package com.codesoom.assignment.errors;

public class UserNotMatchException extends RuntimeException {
    public UserNotMatchException(Long authenticationId, Long id){
        super("User doesn't match.");
    }
}
