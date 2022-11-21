// 1. getProducts -> 목록
// 2. getProduct -> 상세 정보
// 3. createProduct -> 상품 추가
// 4. updateProduct -> 상품 수정
// 5. deleteProduct -> 상품 삭제

package com.codesoom.assignment.product.application;

import com.codesoom.assignment.product.application.exception.ProductNotFoundException;
import com.codesoom.assignment.product.domain.Product;
import com.codesoom.assignment.product.domain.ProductRepository;
import com.codesoom.assignment.support.ProductFixture;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.IdFixture.ID_MIN;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_1;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("ProductService 유닛 테스트")
public class ProductServiceTest {
    private ProductService productService;

    private final ProductRepository productRepository =
            mock(ProductRepository.class);

    @BeforeEach
    void setUpVariable() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        productService = new ProductService(mapper, productRepository);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getProducts_메서드는 {

        @Nested
        @DisplayName("등록된 장난감이 없을 때")
        class Context_with_not_exist_product {
            @BeforeEach
            void setUp() {
                given(productRepository.findAll()).willReturn(List.of());
            }

            @Test
            @DisplayName("빈 리스트를 리턴한다")
            void it_returns_empty_list() {
                assertThat(productService.getProducts()).isEmpty();

                verify(productRepository).findAll();
            }
        }

        @Nested
        @DisplayName("등록된 장난감이 있을 때")
        class Context_with_exist_product {
            @BeforeEach
            void setUp() {
                given(productRepository.findAll())
                        .willReturn(
                                List.of(PRODUCT_1.엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("장난감 목록을 리턴한다")
            void it_returns_product_list() {
                List<Product> products = productService.getProducts();

                assertThat(products).isNotEmpty();
                Product_ID_제외_모든_값_검증(products.get(0), PRODUCT_1);

                verify(productRepository).findAll();
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getProduct_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {
            @BeforeEach
            void setUp() {
                given(productRepository.findById(ID_MIN.value()))
                        .willReturn(
                                Optional.of(PRODUCT_1.엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("해당 id의 장난감 정보를 리턴한다")
            void it_returns_product() {
                Product product = productService.getProduct(ID_MIN.value());

                assertThat(product.getId()).isEqualTo(ID_MIN.value());
                Product_ID_제외_모든_값_검증(product, PRODUCT_1);

                verify(productRepository).findById(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {
            @Test
            @DisplayName("ProductNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.getProduct(ID_MAX.value()))
                        .isInstanceOf(ProductNotFoundException.class);

                verify(productRepository).findById(ID_MAX.value());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class createProduct_메서드는 {

        @Nested
        @DisplayName("유효한 상품 정보가 주어지면")
        class Context_with_valid_product {
            @BeforeEach
            void setUp() {
                given(productRepository.save(PRODUCT_2.엔티티_생성()))
                        .will(invocation -> {
                            Product product = invocation.getArgument(0);

                            return Product.builder()
                                    .id(ID_MIN.value())
                                    .name(product.getName())
                                    .maker(product.getMaker())
                                    .price(product.getPrice())
                                    .imageUrl(product.getImageUrl())
                                    .build();
                        });
            }

            @Test
            @DisplayName("상품을 저장하고 리턴한다")
            void it_returns_product() {
                Product product = productService.createProduct(PRODUCT_2.등록_요청_데이터_생성());

                assertThat(product.getId()).isEqualTo(ID_MIN.value());
                Product_ID_제외_모든_값_검증(product, PRODUCT_2);

                verify(productRepository).save(PRODUCT_2.엔티티_생성());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class updateProduct_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {
            @BeforeEach
            void setUp() {
                given(productRepository.findById(ID_MIN.value()))
                        .willReturn(
                                Optional.of(PRODUCT_1.엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("상품을 수정하고 리턴한다")
            void it_returns_product() {
                Product product = productService.updateProduct(ID_MIN.value(), PRODUCT_2.수정_요청_데이터_생성());

                Product_ID_제외_모든_값_검증(product, PRODUCT_2);

                verify(productRepository).findById(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {
            @Test
            @DisplayName("ProductNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.updateProduct(ID_MAX.value(), PRODUCT_2.수정_요청_데이터_생성()))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class deleteProduct_메서드는 {
        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {
            @BeforeEach
            void setUp() {
                given(productRepository.findById(ID_MIN.value()))
                        .willReturn(
                                Optional.of(PRODUCT_1.엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("상품을 삭제하고 리턴한다")
            void it_deleted() {
                productService.deleteProduct(ID_MIN.value());

                verify(productRepository).delete(PRODUCT_1.엔티티_생성(ID_MIN.value()));
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {
            @Test
            @DisplayName("ProductNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.deleteProduct(ID_MAX.value()))
                        .isInstanceOf(ProductNotFoundException.class);

                verify(productRepository, never()).delete(any(Product.class));
            }
        }
    }

    private void Product_ID_제외_모든_값_검증(Product product, ProductFixture productFixture) {
        assertThat(product.getName()).isEqualTo(productFixture.이름());
        assertThat(product.getMaker()).isEqualTo(productFixture.메이커());
        assertThat(product.getPrice()).isEqualTo(productFixture.가격());
        assertThat(product.getImageUrl()).isEqualTo(productFixture.이미지_URL());
    }
}
