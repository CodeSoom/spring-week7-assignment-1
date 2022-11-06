package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.infra.JpaProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ProductCommandService 클래스")
class ProductCommandServiceTest {

    @Autowired
    private JpaProductRepository productRepository;

    private ProductCommandService productCommandService;

    @BeforeEach
    void setUp() {
        productCommandService = new ProductCommandServiceImpl(productRepository);
    }

    @Nested
    @DisplayName("create 메소드는")
    class Describe_create {

        @Nested
        @DisplayName("입력값이 주어지면")
        class Context_with_valid_input {
            private final ProductData createProductData = createData();

            @Test
            @DisplayName("생성한 상품을 반환한다")
            void it_returns_new_product() {
                Product product = productCommandService.create(createProductData);

                assertThat(product.getId()).isNotNull();
                assertThat(product.getName()).isEqualTo(createProductData.getName());
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Describe_update {

        @Nested
        @DisplayName("수정할 상품 아이디와 입력값이 주어지면")
        class Context_with_valid_input {
            private Long productId;
            private final ProductData updateProductData = updateData();

            @BeforeEach
            void setUp() {
                Product product = productCommandService.create(createData());
                productId = product.getId();
            }

            @Test
            @DisplayName("수정된 상품을 반환한다")
            void it_returns_updated_product() {
                Product updatedProduct = productCommandService.update(productId, updateProductData);

                assertThat(updatedProduct.getName()).isEqualTo(updateProductData.getName());
                assertThat(updatedProduct.getPrice()).isEqualTo(updateProductData.getPrice());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 상품 아이디가 주어지면")
        class Context_with_invalid_id {
            private Long invalidProductId;
            private final ProductData updateProductData = updateData();

            @BeforeEach
            void setUp() {
                Product product = productCommandService.create(createData());
                invalidProductId = product.getId();

                productRepository.delete(product);
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> productCommandService.update(invalidProductId, updateProductData))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("delete 메소드는")
    class Describe_delete {

        @Nested
        @DisplayName("존재하는 상품 아이디가 주어지면")
        class Context_with_valid_id {
            private Long productId;

            @BeforeEach
            void setUp() {
                Product product = productCommandService.create(createData());
                productId = product.getId();
            }

            @Test
            @DisplayName("삭제한 상품을 반환한다")
            void it_throws_exception() {
                Product deletedProduct = productCommandService.delete(productId);
                assertThat(deletedProduct).isNotNull();
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 상품 아이디가 주어지면")
        class Context_with_invalid_id {
            private Long productId;

            @BeforeEach
            void setUp() {
                Product product = productCommandService.create(createData());
                productId = product.getId();

                productRepository.delete(product);
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> productCommandService.delete(productId))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    private ProductData createData() {
        return ProductData.builder()
                .name("TOY")
                .maker("SHOP")
                .price(10000)
                .imageUrl("toy.jpg")
                .build();
    }

    private ProductData updateData() {
        ProductData createdData = createData();

        final String updated = "updated ";
        return ProductData.builder()
                .name(updated + createdData.getName())
                .maker(updated + createdData.getMaker())
                .price(createdData.getPrice() + 5000)
                .imageUrl(updated + createdData.getName())
                .build();
    }
}
