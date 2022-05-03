package com.codesoom.assignment.domain.user;

public interface UserModifier {
    void leave(long userId);
    void modify(User user);
    void join(User user);
}
