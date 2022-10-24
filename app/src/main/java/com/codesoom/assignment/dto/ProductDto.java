package com.codesoom.assignment.dto;

import com.codesoom.assignment.domain.Product;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Generated
public class ProductDto {

    @Generated
    @Setter
    @Getter
    public static class RegisterParam {
        @NotBlank
        private String name;

        @NotBlank
        private String maker;

        @NotNull
        private Integer price;

        private String imageUrl;
    }

    @Generated
    @Getter
    @ToString
    public static class ProductInfo {
        private final Long id;

        private final String name;

        private final String maker;

        private final Integer price;

        private final String imageUrl;

        public ProductInfo(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.maker = product.getMaker();
            this.price = product.getPrice();
            this.imageUrl = product.getImageUrl();
        }
    }
}
