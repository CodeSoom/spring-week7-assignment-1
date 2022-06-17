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
 * User Entity 클래스
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

    private String email;

    private String name;

    private String password;

    // TODO: 기존의 비밀번호를 암호화해야 한다.
    /**
     * 기존의 비밀번호를 매개변수로 주어진 인코더를 통해 암호화한다.
     *
     * @param password 암호화 할 비밀번호
     * @param passwordEncoder 암호화에 사용할 인코더
     */
    public void encodePassword(String password,
                               PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    // TODO: 비밀번호를 인증해야 한다.
    /**
     * User의 비밀번호가 암호화된 비밀번호와 일치하면 true를 반환한다.
     *
     * @param password 인증할 비밀번호
     * @param passwordEncoder 인증에 사용할 인코더
     * @return 비밀번호가 암호화된 비밀번호와 일치하면 true, 일치하지 않으면 false
     */
    public boolean authenticatePassword(String password,
                                        PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }
}
