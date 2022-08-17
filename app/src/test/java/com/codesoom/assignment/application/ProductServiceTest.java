package com.codesoom.assignment.application;

import com.codesoom.assignment.Fixture;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codesoom.assignment.Fixture.PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DisplayName("ProductService 인터페이스의")
public class ProductServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("register 메서드는")
    class Describe_register {
        @Nested
        @DisplayName("상품, 유저 정보가 주어지면")
        class Context_with_productAndUserData {
            private ProductData productData;

            @BeforeEach
            void prepare() {
                UserInquiryInfo info = userService.register(
                        new UserRegisterData(Fixture.EMAIL, Fixture.PASSWORD, Fixture.USER_NAME));

                productData = ProductData.builder()
                        .userId(info.getId())
                        .name(Fixture.PRODUCT_NAME)
                        .description(null)
                        .quantity(Fixture.QUANTITY)
                        .price(PRICE)
                        .build();
            }

            @Test
            @DisplayName("상품을 생성하고 상품 조회 정보를 리턴한다")
            void It_returns_product() {
                Product product = productService.register(productData);

                assertAll(
                        () -> assertThat(product.getName()).isEqualTo(Fixture.PRODUCT_NAME),
                        () -> assertThat(product.getDescription()).isNull(),
                        () -> assertThat(product.getQuantity()).isEqualTo(Fixture.QUANTITY),
                        () -> assertThat(product.getPrice()).isEqualTo(PRICE)
                );
            }
        }
    }
}
