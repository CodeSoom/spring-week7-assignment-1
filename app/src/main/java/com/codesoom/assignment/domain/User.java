package com.codesoom.assignment.domain;

import com.codesoom.assignment.dependency.Container;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String name;

    private String password;

    private boolean deleted = false;

    private EncryptionService encryptionService;

    public User(Long id, String email, String name, String password) {
        encryptionService = Container.encryptionService();

        this.id = id;
        this.email = email;
        this.name = name;
        protectPassword(password);
    }

    public User() {}

    public void changeWith(String name, String password) {
        this.name = name;
        protectPassword(password);
    }

    private void protectPassword(String aChangedPassword) {
        this.password = encryptionService.encryptedValue(aChangedPassword);
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        String encryptedPassword = encryptionService.encryptedValue(password);
        return !deleted && encryptedPassword.equals(this.password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDestroyed() {
        return deleted;
    }
}
