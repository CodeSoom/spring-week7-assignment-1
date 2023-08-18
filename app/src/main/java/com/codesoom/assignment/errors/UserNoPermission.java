package com.codesoom.assignment.errors;

public class UserNoPermission extends RuntimeException{
    public UserNoPermission(String message) {
        super("No user permissions : " + message);
    }
}
