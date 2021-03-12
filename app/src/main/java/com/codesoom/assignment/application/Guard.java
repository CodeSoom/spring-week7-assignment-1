package com.codesoom.assignment.application;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class Guard {

    public boolean checkIdMatch(Authentication authentication, Long id) {
        if (authentication == null) {
            return false;
        }
        Long userDetailId = (Long) authentication.getPrincipal();
        return id.equals(userDetailId);
    }
}
