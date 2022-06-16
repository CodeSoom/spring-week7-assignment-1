package com.codesoom.assignment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Product Entity에 대한 테스트 클래스
 */
public class ProductTest {
    private static final Long ID = 1L;
    private static final String NAME = "쥐돌이";
    private static final String MAKER = "냥이월드";
    private static final int PRICE = 5000;

    private Product product;

    // TODO: Builder로 Product를 생성할 수 있어야 한다.
    @Nested
    @DisplayName("Product는")
    class Describe_product {
        @Nested
        @DisplayName("빌더를 통해")
        class Context_by_builder {
            Product subject() {
                return product = Product.builder()
                        .id(ID)
                        .name(NAME)
                        .maker(MAKER)
                        .price(PRICE)
                        .build();
            }

            @Test
            @DisplayName("객체를 생성한다")
            void It_creates_product() {
                Product product = subject();

                assertThat(product.getId()).isEqualTo(ID);
                assertThat(product.getName()).isEqualTo(NAME);
                assertThat(product.getMaker()).isEqualTo(MAKER);
                assertThat(product.getPrice()).isEqualTo(PRICE);
                assertThat(product.getImageUrl()).isNull();
            }
        }
    }

    // TODO: name, maker, price, imageUrl을 수정할 수 있어야 한다.
}
