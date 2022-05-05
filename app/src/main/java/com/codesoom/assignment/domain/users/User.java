package com.codesoom.assignment.domain.users;

import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    protected User() {
    }

    private User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static User of(String name, String email) {
        return new User(name, email);
    }

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User update(User user) {
        this.name = user.name;
        this.email = user.email;
        return this;
    }

    /**
     * 주어진 비밀번호를 암호화 처리 후 password 필드에 초기화 합니다.
     *
     * @param rawPassword 입력받은 비밀번호
     * @param passwordEncoder 비밀번호 암호화 인코더
     */
    public void changePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    public boolean authenticate(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

}
