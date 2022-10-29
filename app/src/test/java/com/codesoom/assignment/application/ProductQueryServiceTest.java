package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.infra.JpaProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ProductQueryService 클래스")
class ProductQueryServiceTest {

    @Autowired
    private JpaProductRepository productRepository;

    private ProductQueryService productQueryService;

    @BeforeEach
    void setUp() {
        productQueryService = new ProductQueryServiceImpl(productRepository);
    }

    @AfterEach
    void clear() {
        productRepository.deleteAll();
    }

    @Nested
    @DisplayName("getAll 메소드는")
    class Describe_getAll {

        @Nested
        @DisplayName("저장된 상품이 있으면")
        class Context_with_products {
            private final int PRODUCT_SIZE = 5;

            @BeforeEach
            void setUp() {
                for (int i = 0; i < PRODUCT_SIZE; i++) {
                    productRepository.save(getProduct());
                }
            }

            @Test
            @DisplayName("모든 상품을 반환한다")
            void it_returns_all_products() {
                List<Product> products = productQueryService.getAll();
                assertThat(products).hasSize(PRODUCT_SIZE);
            }
        }
    }

    @Nested
    @DisplayName("get 메소드는")
    class Describe_get {

        @Nested
        @DisplayName("존재하는 상품의 아이디가 주어지면")
        class Context_with_product_id {
            private Long productId;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                productId = product.getId();
            }

            @Test
            @DisplayName("해당 아이디의 상품을 반환한다")
            void it_returns_all_products() {
                Product product = productQueryService.get(productId);
                assertThat(product).isNotNull();
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 상품의 아이디가 주어지면")
        class Context_with_not_exist_id {
            private Long productId;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                productId = product.getId();

                productRepository.delete(product);
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> productQueryService.get(productId))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    private Product getProduct() {
        return Product.builder()
                .name("TOY")
                .maker("SHOP")
                .price(10000)
                .imageUrl("toy.jpg")
                .build();
    }
}
