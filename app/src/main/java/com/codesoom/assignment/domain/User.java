package com.codesoom.assignment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 사용자 정보를 다룬다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {
    /** 사용자 식별자 */
    @Id
    @GeneratedValue
    private Long id;

    /** 사용자 이름 */
    private String name;

    /** 사용자 이메일 */
    private String email;

    /** 사용자 비밀번호 */
    private String password;

    /** 사용자 삭제여부 */
    private boolean deleted = false;

    @Builder
    public User(Long id, String name, String email, String password, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.deleted = deleted;
    }

    /** 사용자의 삭제 여부를 true 로 변경한다 */
    public void delete() {
        deleted = true;
    }

    /** 사용자의 이름을 수정한다 */
    public void updateName(String name) {
        this.name = name;
    }

    /** 사용자의 비밀번호를 암호화 하여 저장한다 */
    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    /** 사용자가 삭제되어 있지 않고 비밀번호가 일치하면 true, 하나라도 틀리다면 false 를 리턴한다 */
    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }
}
