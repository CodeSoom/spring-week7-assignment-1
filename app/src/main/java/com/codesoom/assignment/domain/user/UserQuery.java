package com.codesoom.assignment.domain.user;

public interface UserQuery {
    User findUser(Long userId);
    User findByEmail(String email);
}
