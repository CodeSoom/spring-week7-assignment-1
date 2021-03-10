package com.codesoom.assignment.errors;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(Long id) {
        super("Access denied: " + id);
    }
}
