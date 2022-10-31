package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.ProductCommand;
import com.codesoom.assignment.application.dto.ProductCommand.Update;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.ProductDto;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.mapper.ProductFactory;
import com.codesoom.assignment.utils.ProductSampleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("ProductService 클래스")
class ProductServiceTest {
    @DataJpaTest
    class JpaTest {
        @Autowired
        ProductRepository repository;
        ProductService service;

        private final ProductFactory productMapper = ProductFactory.INSTANCE;

        public ProductFactory getProductMapper() {
            return productMapper;
        }

        public ProductRepository getProductRepository() {
            return repository;
        }

        public ProductService getProductService() {
            if (service == null) {
                service = new ProductService(repository, productMapper);
            }
            return service;
        }
    }

    @Nested
    @DisplayName("getProducts 메소드는")
    class Describe_getProducts {
        @Nested
        @DisplayName("데이터가 존재한다면")
        class Context_with_existed_data extends JpaTest {
            private final List<Product> givenProducts = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenProducts.add(getProductRepository().save(ProductSampleFactory.createProduct()));
                givenProducts.add(getProductRepository().save(ProductSampleFactory.createProduct()));
            }

            @Test
            @DisplayName("모든 상품을 리턴한다")
            void getProducts() {
                final List<Product> actualProducts = getProductService().getProducts();

                assertThat(actualProducts).isNotEmpty();

                assertThat(actualProducts).hasSize(givenProducts.size());
            }
        }

        @Nested
        @DisplayName("데이터가 존재하지 않는다면")
        class Context_with_not_existed_data extends JpaTest {
            @Test
            @DisplayName("빈 컬렉션을 리턴한다")
            void it_returns_empty_collection() {
                final List<Product> actualProducts = getProductService().getProducts();

                assertThat(actualProducts).isEmpty();
            }
        }


    }

    @Nested
    @DisplayName("getProduct 메소드는")
    class Describe_getProduct {
        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id extends JpaTest {
            private Product givenProduct;

            @BeforeEach
            void prepare() {
                givenProduct = getProductRepository().save(ProductSampleFactory.createProduct());
            }

            @Test
            @DisplayName("상품을 찾아서 리턴한다")
            void it_returns_searched_product() {
                final Product actualProduct = getProductService().getProduct(givenProduct.getId());

                assertThat(actualProduct).isNotNull();
                assertThat(actualProduct.getName()).isEqualTo(givenProduct.getName());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id extends JpaTest {
            @Test
            @DisplayName("예외를 던진다")
            void getProductWithNotExsitedId() {
                assertThatThrownBy(() -> getProductService().getProduct(9999L))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("createProduct 메소드는")
    class Describe_createProduct {
        @Nested
        @DisplayName("새로운 상품이 주어지면")
        class Context_with_new_product extends JpaTest {
            private final ProductDto.RegisterParam givenRequest = ProductSampleFactory.createRequestParam();

            @Test
            @DisplayName("DB에 등록하고 등록된 상품을 리턴한다")
            void it_returns_registered_product() {
                final Product actualProduct = getProductService().createProduct(getProductMapper().of(givenRequest));

                assertThat(actualProduct.getName()).isEqualTo(givenRequest.getName());
                assertThat(actualProduct.getMaker()).isEqualTo(givenRequest.getMaker());
                assertThat(actualProduct.getPrice()).isEqualTo(givenRequest.getPrice());
            }
        }
    }

    @Nested
    @DisplayName("updateProduct 메소드는")
    class Describe_updateProduct {
        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id extends JpaTest {
            private Product savedProduct;

            @BeforeEach
            void prepare() {
                savedProduct = getProductRepository().save(ProductSampleFactory.createProduct());
            }

            @Test
            @DisplayName("상품을 수정하고 수정된 상품을 리턴한다")
            void it_returns_modified_product() {
                ProductCommand.Update.UpdateBuilder builder = ProductCommand.Update.builder();
                System.out.println(builder.toString());

                final ProductCommand.Update command = builder
                        .id(savedProduct.getId())
                        .name("수정_" + savedProduct.getName())
                        .maker("수정_" + savedProduct.getMaker())
                        .price(savedProduct.getPrice() + 1000)
                        .build();

                final Product actualProduct = getProductService().updateProduct(command);

                assertThat(actualProduct.getId()).isEqualTo(command.getId());
                assertThat(actualProduct.getName()).isEqualTo(command.getName());
                assertThat(actualProduct.getMaker()).isEqualTo(command.getMaker());
                assertThat(actualProduct.getPrice()).isEqualTo(command.getPrice());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id extends JpaTest {
            private final Long INVALID_PRODUCT_ID = 9999L;
            private final Update givenCommand = getProductMapper().of(INVALID_PRODUCT_ID, ProductSampleFactory.createRequestParam());

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> getProductService().updateProduct(givenCommand)).isInstanceOf(ProductNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteProduct 메소드는")
    class Describe_deleteProduct {
        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id extends JpaTest {
            private Product savedProduct;

            @BeforeEach
            void prepare() {
                savedProduct = getProductRepository().save(ProductSampleFactory.createProduct());
            }

            @Test
            @DisplayName("상품을 삭제하고 삭제된 상품을 리턴한다")
            void it_deletes_searched_product() {
                Product deletedProduct = getProductService().deleteProduct(savedProduct.getId());

                Optional<Product> findProduct = getProductRepository().findById(savedProduct.getId());

                assertThat(findProduct).isEmpty();

                assertThat(deletedProduct.getId()).isEqualTo(savedProduct.getId());
                assertThat(deletedProduct.getName()).isEqualTo(savedProduct.getName());
                assertThat(deletedProduct.getMaker()).isEqualTo(savedProduct.getMaker());
                assertThat(deletedProduct.getPrice()).isEqualTo(savedProduct.getPrice());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id extends JpaTest {
            private final Long INVALID_PRODUCT_ID = 9999L;

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> getProductService().deleteProduct(INVALID_PRODUCT_ID)).isInstanceOf(ProductNotFoundException.class);
            }
        }
    }
}
