package com.codesoom.assignment.application.authentication;


import com.codesoom.assignment.domain.auth.AuthenticationCommand;
import com.codesoom.assignment.domain.auth.AuthenticationQuery;
import com.codesoom.assignment.domain.auth.AuthenticationService;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.dto.auth.AuthRequestData;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationCommand authenticationCommand;
    private final AuthenticationQuery authenticationQuery;

    public AuthenticationServiceImpl(
            AuthenticationCommand authenticationCommand,
            AuthenticationQuery authenticationQuery
    ) {
        this.authenticationCommand = authenticationCommand;
        this.authenticationQuery = authenticationQuery;
    }

    @Override
    public String login(AuthRequestData authRequestData) {
        User user = authRequestData.toEntity();
        return authenticationCommand.login(user);
    }

    @Override
    public boolean isAccessible(String token) {
        return false;
    }
}
