package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductData {
    private final Long userId;
    private final String name;
    private final String description;
    private final Integer quantity;
    private final Integer price;

    @Builder
    public ProductData(Long userId, String name, String description, Integer quantity, Integer price) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public Product toProduct() {
        return Product.builder()
                .userId(this.userId)
                .name(this.name)
                .description(this.description)
                .quantity(this.quantity)
                .price(this.price)
                .build();
    }
}
