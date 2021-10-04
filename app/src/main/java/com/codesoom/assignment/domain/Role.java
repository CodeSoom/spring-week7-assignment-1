package com.codesoom.assignment.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    @Getter
    private String name;

    public Role(String name) {
        this.name = name;
    }

    protected Role() {
    }
}
