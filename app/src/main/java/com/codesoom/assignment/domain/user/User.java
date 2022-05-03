package com.codesoom.assignment.domain.user;

import com.codesoom.assignment.domain.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String name;
    private String password;
    private boolean deleted = false;

    private User(UserBuilder userBuilder) {
        this.id = id;
        this.email = userBuilder.email;
        this.name = userBuilder.name;
        this.password = userBuilder.password;
    }

    public void changeWith(User source) {
        name = source.name;
        password = source.password;
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        return !deleted && password.equals(this.password);
    }

    public static class UserBuilder implements Builder<User> {
        private Long id;
        private String email;
        private String name;
        private String password;

        public static UserBuilder builder() {
            return new UserBuilder();
        }

        public UserBuilder id(long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        @Override
        public User build() {
            return new User(this);
        }
    }
}