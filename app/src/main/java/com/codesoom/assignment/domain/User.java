package com.codesoom.assignment.domain;

import com.codesoom.assignment.errors.InvalidParamException;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Generated
@Entity
@Getter
@ToString(of = {"id", "name", "password", "email"})
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String password;

    @Builder.Default
    private boolean deleted = false;

    protected User() {
    }

    @Builder
    public User(Long id, String name, String password, String email, boolean deleted) {
        if (Strings.isBlank(name)) {
            throw new InvalidParamException("이름이 비어있습니다.");
        }
        if (Strings.isBlank(password)) {
            throw new InvalidParamException("비밀번호가 비어있습니다.");
        }

        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.deleted = deleted;
    }

    public void modifyUserInfo(User source) {
        name = source.name;
        password = source.password;
    }

    public void deleteUser() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        return !deleted && password.equals(this.password);
    }
}
