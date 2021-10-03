package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 회원 정보를 저장하고 처리합니다.
 */
@Entity
@Builder
@Getter
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String name;

    @Builder.Default
    private String password = "";

    @Builder.Default
    private boolean deleted = false;

    public User() {}

    public User(Long id, String email, String name, String password, boolean deleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.deleted = deleted;
    }

    /**
     * 회원 정보를 수정합니다.
     * @param source 회원 수정 정보
     */
    public void changeWith(User source) {
        name = source.name;
        password = source.password;
    }

    /**
     * 회원을 탈퇴 처리합니다.
     */
    public void destroy() {
        deleted = true;
    }

    /**
     * 회원 정보를 인증하고 리턴합니다.
     * @param password 비밀번호
     * @param passwordEncoder 비밀번호 인코더
     * @return 인증결과
     */
    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }

    /**
     * 비밀번호를 변경합니다.
     * @param password 변경할 비밀번호
     * @param passwordEncoder 비밀번호 인코더
     */
    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
