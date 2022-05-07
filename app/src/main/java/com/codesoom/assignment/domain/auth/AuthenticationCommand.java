package com.codesoom.assignment.domain.auth;

import com.codesoom.assignment.domain.user.User;

public interface AuthenticationCommand {
    String login(User user);
}
