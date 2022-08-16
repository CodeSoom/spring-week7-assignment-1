package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DisplayName("ProductService 인터페이스의")
public class ProductServiceTest {
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String USER_NAME = "박범진";
    public static final String PRODUCT_NAME = "상품명";
    public static final int QUANTITY = 10;
    public static final int PRICE = 10000;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @Nested
    @DisplayName("register 메서드는")
    class Describe_register {
        @Nested
        @DisplayName("상품, 유저 정보가 주어지면")
        class Context_with_productAndUserData {
            private ProductData productData;

            @BeforeEach
            void prepare() {
                UserInquiryInfo info = userService.register(new UserRegisterData(EMAIL, PASSWORD, USER_NAME));
                productData = ProductData.builder()
                        .userId(info.getId())
                        .name(PRODUCT_NAME)
                        .description(null)
                        .quantity(QUANTITY)
                        .price(PRICE)
                        .build();
            }

            @Test
            @DisplayName("상품을 생성하고 상품 조회 정보를 리턴한다")
            void It_returns_product() {
                Product product = productService.register(productData);

                assertAll(
                        () -> assertThat(product.getName()).isEqualTo(PRODUCT_NAME),
                        () -> assertThat(product.getDescription()).isNull(),
                        () -> assertThat(product.getQuantity()).isEqualTo(QUANTITY),
                        () -> assertThat(product.getPrice()).isEqualTo(PRICE)
                );
            }
        }
    }
}
