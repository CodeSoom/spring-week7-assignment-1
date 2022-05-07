package com.codesoom.assignment.domain.auth;


import com.codesoom.assignment.dto.auth.AuthRequestData;

public interface AuthenticationService {
    String login(AuthRequestData authRequestData);

    boolean isAccessible(String token);

}
