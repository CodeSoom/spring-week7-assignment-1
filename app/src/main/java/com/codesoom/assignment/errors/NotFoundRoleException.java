package com.codesoom.assignment.errors;

public class NotFoundRoleException extends RuntimeException {
    public NotFoundRoleException(String name) {
        super("Not Found Role: " + name);
    }
}
