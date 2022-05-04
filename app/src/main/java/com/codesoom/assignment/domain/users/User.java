package com.codesoom.assignment.domain.users;

import lombok.Getter;

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

    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * id를 제외한 필드 초기화
     */
    public static User withoutId(String name, String email, String password) {
        return new User(name, email, password);
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
        this.password = user.password;
        return this;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

}
