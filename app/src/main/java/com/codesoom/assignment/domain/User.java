package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Generated
@Entity
@Getter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String password;

    private boolean deleted = false;

    protected User() {
    }

    @Builder
    public User(Long id, String name, String email, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.deleted = deleted != null && deleted;
    }

    public void modifyUserInfo(User source) {
        name = source.name;
    }

    public void modifyPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void deleteUser() {
        deleted = true;
    }

    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }
}
