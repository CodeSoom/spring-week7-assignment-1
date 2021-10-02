package com.codesoom.assignment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void creationWithBuilder() {
        Product product = Product.builder()
                .id(1L)
                .name("쥐돌이")
                .maker("냥이월드")
                .price(5000)
                .build();

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("쥐돌이");
        assertThat(product.getMaker()).isEqualTo("냥이월드");
        assertThat(product.getPrice()).isEqualTo(5000);
        assertThat(product.getImageUrl()).isNull();
    }

    @Test
    void setCreator() {
        long userId = 1L;
        String email = "hello@email.com";
        String name = "name1";

        User user = User.builder()
                .id(userId)
                .email(email)
                .name(name)
                .build();

        Product product = new Product();

        product.setCreator(user);

        assertThat(product.getCreator().getId()).isEqualTo(userId);
        assertThat(product.getCreator().getEmail()).isEqualTo(email);
        assertThat(product.getCreator().getName()).isEqualTo(name);
    }

    @Test
    void changeWith() {
        Product product = Product.builder()
                .id(1L)
                .name("쥐돌이")
                .maker("냥이월드")
                .price(5000)
                .build();

        product.changeWith(Product.builder()
                .name("쥐순이")
                .maker("코드숨")
                .price(10000)
                .build());

        assertThat(product.getName()).isEqualTo("쥐순이");
        assertThat(product.getMaker()).isEqualTo("코드숨");
        assertThat(product.getPrice()).isEqualTo(10000);
    }

    @Nested
    @DisplayName("isAuthenticated 메소드는")
    class Describe_isAuthenticated{

        @Nested
        @DisplayName("유효한 권한일 경우")
        class Context_with_valid_authentication_valid{

        }

        @Nested
        @DisplayName("유효하지 못한 권한일 경우")
        class Context_with_invalid_authentication_valid{

        }
    }

}
