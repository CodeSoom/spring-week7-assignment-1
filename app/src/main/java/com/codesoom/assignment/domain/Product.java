package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    private String description;

    private Integer quantity;

    private Integer price;

    public Product() {}

    @Builder
    public Product(Long userId, String name, String description, Integer quantity, Integer price) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
}
