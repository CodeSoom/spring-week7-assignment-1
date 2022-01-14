package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 사용자 정보를 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private String email = "";

    @Builder.Default
    private String name = "";

    @Builder.Default
    private String password = "";

    @Builder.Default
    private boolean deleted = false;

    @Builder
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Builder
    public User(Long id, String email, String name, String password, boolean deleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.deleted = deleted;
    }

    /**
     * 사용자 정보를 수정한다.
     *
     * @param source 사용자 수정 정보
     */
    public void changeWith(User source) {
        name = source.name;
        password = source.password;
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param password        변경할 비밀번호
     * @param passwordEncoder 비밀번호 인코더
     */
    public void changePassword(String password,
                               PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    /**
     * 사용자를 탈퇴 처리한다.
     */
    public void destroy() {
        deleted = true;
    }

    /**
     * 사용자 정보를 인증하고 리턴한다.
     *
     * @param password        비밀번호
     * @param passwordEncoder 비밀번호 인코더
     * @return 인증에 성공하면 true를 반환한다.
     */
    public boolean authenticate(String password
            , PasswordEncoder passwordEncoder
    ) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }
}
