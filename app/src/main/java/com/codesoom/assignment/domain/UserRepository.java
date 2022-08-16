package com.codesoom.assignment.domain;

public interface UserRepository {
    /**
     * 유저를 저장하고 리턴한다.
     *
     * @param user 유저
     * @return 유저
     */
    User save(User user);
}
