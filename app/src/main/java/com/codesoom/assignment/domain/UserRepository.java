package com.codesoom.assignment.domain;

import java.util.Optional;

public interface UserRepository {
    /**
     * 신규 회원을 추가하고 추가된 회원을 리턴한다.
     * @param user 신규 회원
     * @return 추가된 회원정보
     */
    User save(User user);

    /**
     * 회원 ID로 검색하고 검색된 회원정보를 리턴한다.
     * @param id 회원 ID
     * @return 검색된 회원정보
     */
    Optional<User> findById(Long id);

    /**
     * 회원 이메일로 검색하고 검색된 회원정보를 리턴한다.
     * @param email 회원 이메일
     * @return 검색된 회원정보
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일에 해당하는 회원 등록여부를 리턴한다.
     * @param email 회원 이메일
     * @return 회원 등록여부
     */
    boolean existsByEmail(String email);

    /**
     * 활동중인 회원목록에서 회원 ID로 검색하고 검색된 회원정보를 리턴한다.
     * @param id 회원 ID
     * @return 검색된 회원정보
     */
    Optional<User> findByIdAndDeletedIsFalse(Long id);
}
