package com.codesoom.assignment.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDataTest {
    @Test
    @DisplayName("toString 메서드 테스트")
    void toStringTest() {
        assertThat(ProductData.builder().id(1L).imageUrl("image").toString())
                .contains("1")
                .contains("image");
    }

    @Test
    @DisplayName("[get/set]id 메서드 테스트")
    void idTest() {
        final long givenID = 1;
        final ProductData productData = new ProductData();

        productData.setId(givenID);
        assertThat(productData.getId()).isEqualTo(givenID);
    }

    @Test
    @DisplayName("[get/set]imageUrl 메서드 테스트")
    void imageUrlTest() {
        final String givenImageUrl = "image";
        final ProductData productData = new ProductData();

        productData.setImageUrl(givenImageUrl);
        assertThat(productData.getImageUrl()).isEqualTo(givenImageUrl);
    }
}
