package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String name;

    private String password;

    private boolean deleted = false;

    @Builder
    public User(Long id, String email, String name, String password, boolean deleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.deleted = deleted;
    }

    /**
     * source 로 들어온 사용자 정보로 사용자 정보를 업데이트한다.
     * 단, null 이 들어온 경우에는 기존의 값을 그대로 유지한다.
     * @param source 사용자 정보 변경에 쓰이는 User 객체
     */
    public void changeWith(User source) {
        name = Optional.ofNullable(source.name).orElse(name);
        password = Optional.ofNullable(source.password).orElse(password);
    }

    /**
     * 암호화된 패스워드를 저장한다.
     * @param encodedPassword 인코딩된 패스워드
     */
    public void encryptPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        return !deleted && password.equals(this.password);
    }
}
