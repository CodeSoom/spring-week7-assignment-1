package com.codesoom.assignment.domain.user;

public interface UserQuery {
    String join(User user);
    User login(User user);
}
