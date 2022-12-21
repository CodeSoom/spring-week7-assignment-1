package com.codesoom.assignment.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codesoom.assignment.support.IdFixture.ID_MIN;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_1;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_2;
import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {
    @Test
    @DisplayName("Product 빌더 테스트")
    void creationWithBuilder() {
        Product product = PRODUCT_1.엔티티_생성(ID_MIN.value());

        assertThat(product.getId()).isEqualTo(ID_MIN.value());
        assertThat(product.getName()).isEqualTo(PRODUCT_1.이름());
        assertThat(product.getMaker()).isEqualTo(PRODUCT_1.메이커());
        assertThat(product.getPrice()).isEqualTo(PRODUCT_1.가격());
        assertThat(product.getImageUrl()).isEqualTo(PRODUCT_1.이미지_URL());
    }

    @Test
    @DisplayName("Product 수정 테스트")
    void changeWith() {
        Product product = PRODUCT_1.엔티티_생성(ID_MIN.value());

        product.changeWith(PRODUCT_2.엔티티_생성());

        assertThat(product.getName()).isEqualTo(PRODUCT_2.이름());
        assertThat(product.getMaker()).isEqualTo(PRODUCT_2.메이커());
        assertThat(product.getPrice()).isEqualTo(PRODUCT_2.가격());
    }

    @Test
    @DisplayName("객체 비교 테스트")
    void equals_and_hashcode() {
        Product product1 = PRODUCT_2.엔티티_생성(ID_MIN.value());
        Product product2 = PRODUCT_2.엔티티_생성(ID_MIN.value());

        assertThat(product1).isEqualTo(product2);
    }
}
