package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Builder.Default
    private String name = "";

    @Setter
    @Builder.Default
    private String email = "";

    @Setter
    @Builder.Default
    private String password = "";

    @Builder.Default
    private boolean deleted = false;

    public User updateWith(User user) {
        this.email = user.getEmail();

        return this;
    }


    public void changePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return !deleted && passwordEncoder.matches(password, this.password);
    }
}
