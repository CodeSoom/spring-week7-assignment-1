package com.codesoom.assignment.errors;

public class UserForbiddenException extends RuntimeException{
    public UserForbiddenException(Long id) {super("User forbidden" + id);}
}
