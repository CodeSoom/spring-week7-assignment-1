package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ProductQueryServiceTest {
    private ProductQueryService productService;

    private final ProductRepository productRepository =
            mock(ProductRepository.class);

    @BeforeEach
    void setupInstance() {
        productService = new ProductQueryService(productRepository);
    }

    @BeforeEach
    void setUpMocks() {
        Product product = Product.builder()
                .id(1L)
                .name("쥐돌이")
                .maker("냥이월드")
                .price(5000)
                .build();

        given(productRepository.findAll()).willReturn(List.of(product));

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class ProductList {
        @Test
        @DisplayName("상품 목록을 반환한다.")
        void returnProducts() {
            List<Product> products = productService.getProducts();

            assertThat(products).isNotEmpty();

            Product product = products.get(0);

            assertThat(product.getName()).isEqualTo("쥐돌이");
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class ProductDetails {
        @Test
        @DisplayName("상품 상세를 반환한다.")
        void returnProductDetails() {
            Product product = productService.getProduct(1L);

            assertThat(product).isNotNull();
            assertThat(product.getName()).isEqualTo("쥐돌이");
        }

        @Nested
        @DisplayName("해당 식별자의 상품을 찾을 수 없으면")
        class WithNotFoundId {
            @Test
            @DisplayName("상품을 찾을 수 없다는 내용의 예외를 던진다.")
            void returnError() {
                assertThatThrownBy(() -> productService.getProduct(1000L))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }
}
