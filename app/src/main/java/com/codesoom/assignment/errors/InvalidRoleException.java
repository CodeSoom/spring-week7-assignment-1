package com.codesoom.assignment.errors;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException() {
        super("You don't have a right to request");
    }
}
