package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * @return 인증결과
     */
    public boolean authenticate(String password
            , PasswordEncoder passwordEncoder
    ) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }
}
