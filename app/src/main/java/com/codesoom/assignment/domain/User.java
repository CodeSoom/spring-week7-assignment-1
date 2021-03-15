package com.codesoom.assignment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/** 사용자 정보를 다룬다. */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class User {
    /** 사용자 식별자 */
    @Id
    @GeneratedValue
    private Long id;

    /** 사용자 이름 */
    @Builder.Default
    private String name = "";

    /** 사용자 이메일 */
    @Builder.Default
    private String email = "";

    /** 사용자 비밀번호 */
    @Builder.Default
    private String password = "";

    /** 사용자 삭제여부 */
    @Builder.Default
    private boolean deleted = false;

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
