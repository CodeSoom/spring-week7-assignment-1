package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    @Builder.Default
    private boolean deleted = false;

    /**
     * 회원 정보를 수정합니다.
     * @param name 변경할 이름
     * @param password 변경할 비밀번호
     */
    public void update(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void destroy() {
        deleted = true;
    }
}
