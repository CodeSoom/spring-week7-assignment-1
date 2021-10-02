package com.codesoom.assignment.domain;

import java.util.Optional;

/**
 * 회원 데이터를 다루는 명령을 정의합니다.
 */
public interface UserRepository {
    User save(User user);

    /**
     * 해당 이메일의 회원 유무를 리턴합니다.
     * @param email 회원 이메일
     * @return 회원 유무
     */
    boolean existsByEmail(String email);

    /**
     * 해당 식별자의 회원 유무를 리턴합니다.
     * @param id 회원 식별자
     * @return 회원 유무
     */
    boolean existsById(Long id);

    /**
     * 해당 식별자의 회원을 리턴합니다.
     * @param id 회원 식별자
     * @return 회원
     */
    Optional<User> findById(Long id);

    /**
     * 탈퇴하지 않은 해당 식별자의 회원을 리턴합니다.
     * @param id 회원 식별자
     * @return 회원
     */
    Optional<User> findByIdAndDeletedIsFalse(Long id);

    /**
     * 해당 이메일의 회원을 리턴합니다.
     * @param email 회원 이메일
     * @return 회원
     */
    Optional<User> findByEmail(String email);
}
