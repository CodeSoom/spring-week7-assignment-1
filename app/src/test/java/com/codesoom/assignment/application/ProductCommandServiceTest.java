package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProductCommandServiceTest {
    private ProductCommandService productService;

    private final ProductRepository productRepository = mock(ProductRepository.class);

    private ProductData productDataFixture;
    private Product productFixture;

    @BeforeEach
    void setupFixture() {
        productDataFixture = ProductData.builder()
                .name("쥐돌이")
                .maker("냥이월드")
                .price(5000)
                .build();

        productFixture = Product.builder()
                .id(1L)
                .name("쥐돌이")
                .maker("냥이월드")
                .price(5000)
                .build();
    }

    @BeforeEach
    void setupInstance() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();

        productService = new ProductCommandService(mapper, productRepository);
    }

    @Nested
    @DisplayName("상품 생성")
    class ProductCreation {
        @BeforeEach
        void setupMocks() {
            given(productRepository.save(any(Product.class))).willReturn(productFixture);
        }

        @Test
        @DisplayName("상품을 저장하고 저장된 상품을 반환한다.")
        void saveProductAndReturnIt() {
            Product product = productService.createProduct(productDataFixture);

            verify(productRepository).save(any(Product.class));

            assertThat(product.getName()).isEqualTo(productDataFixture.getName());
            assertThat(product.getMaker()).isEqualTo(productDataFixture.getMaker());
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class ProductModification {
        @BeforeEach
        void setupMocks() {
            given(productRepository.save(any(Product.class))).willReturn(productFixture);
            given(productRepository.findById(1L)).willReturn(Optional.of(productFixture));
        }

        @Test
        @DisplayName("상품을 수정하고 수정된 상품을 반환한다.")
        void updateProductAndReturnIt() {
            Product product = productService.updateProduct(1L, productDataFixture);

            assertThat(product.getId()).isEqualTo(1L);
            assertThat(product.getName()).isEqualTo(productDataFixture.getName());
        }

        @Nested
        @DisplayName("해당 식별자의 상품을 찾을 수 없으면")
        class WithNonExistentId {
            @Test
            @DisplayName("상품을 찾을수 없다는 내용의 예외를 던진다.")
            void returnError() {
                assertThatThrownBy(() -> productService.updateProduct(1000L, productDataFixture))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class ProductDeletion {
        @BeforeEach
        void setupMocks() {
            given(productRepository.save(any(Product.class))).willReturn(productFixture);
            given(productRepository.findById(1L)).willReturn(Optional.of(productFixture));
        }

        @Test
        @DisplayName("상품을 삭제하고 삭제된 상품을 반환한다.")
        void deleteProductAndReturnIt() {
            productService.deleteProduct(1L);

            verify(productRepository).delete(any(Product.class));
        }

        @Nested
        @DisplayName("해당 식별자의 상품을 찾을 수 없으면")
        class WithNonExistentId {
            @Test
            @DisplayName("상품을 찾을수 없다는 내용의 예외를 던진다.")
            void returnError() {
                assertThatThrownBy(() -> productService.deleteProduct(1000L))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }
}
