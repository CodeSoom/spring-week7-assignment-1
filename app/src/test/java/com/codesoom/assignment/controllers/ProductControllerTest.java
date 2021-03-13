package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.github.dozermapper.core.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private final Product givenProduct = Product.builder()
            .id(1L)
            .name("쥐돌이")
            .maker("냥이월드")
            .price(5000)
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(authenticationService.parseToken(VALID_TOKEN)).willReturn(1L);

        given(authenticationService.parseToken(INVALID_TOKEN))
                .willThrow(new InvalidTokenException(INVALID_TOKEN));
    }

    @Nested
    @DisplayName("[GET] /products 요청은")
    class Describe_products_get {
        @BeforeEach
        void setup() {
            given(productService.getProducts()).willReturn(List.of(givenProduct));
        }

        @Test
        @DisplayName("ok 와 저장된 product 들을 응답한다.")
        void It_respond_ok_and_products() throws Exception {
            mockMvc.perform(
                    get("/products")
                            .accept(MediaType.APPLICATION_JSON_UTF8)
            )
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("쥐돌이")));
        }
    }

    @Nested
    @DisplayName("[GET] /products/{id} 요청은")
    class Describe_products_id_get {
        @BeforeEach
        void setup() {
            given(productService.getProduct(1L)).willReturn(givenProduct);

            given(productService.getProduct(1000L))
                    .willThrow(new ProductNotFoundException(1000L));
        }

        @Nested
        @DisplayName("주어진 id에 해당하는 product 가 존재할 때")
        class Context_when_exists_given_id_product {
            @Test
            @DisplayName("ok 와 id 에 해당하는 product 를 응답한다.")
            void It_respond_okj_and_product() throws Exception {
                mockMvc.perform(
                        get("/products/1")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("쥐돌이")));
            }
        }

        @Nested
        @DisplayName("주어진 id에 해당하는 product 가 존재하지 않을 때")
        class Context_when_not_exists_given_id_product {
            @Test
            @DisplayName("not found 를 응답한다.")
            void It_respond_not_found() throws Exception {
                mockMvc.perform(get("/products/1000"))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("[POST] /products 요청은")
    class Describe_post_products {
        @Nested
        @DisplayName("주어진 요청에 access token 이 없을 때")
        class Context_without_access_token {
            @Test
            @DisplayName("unauthorized 를 응답한다.")
            void It_respond_unauthorized() throws Exception {
                mockMvc.perform(
                        post("/products")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("주어진 요청의 access token 이 올바르지 않을 때")
        class Context_with_invalid_access_token {
            @Test
            @DisplayName("unauthorized 를 응답한다.")
            void It_respond_unauthorized() throws Exception {
                mockMvc.perform(
                        post("/products")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                                .header("Authorization", "Bearer " + INVALID_TOKEN)
                )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("주어진 요청의 access token 이 올바를 때")
        class Context_with_valid_access_token {
            @BeforeEach
            void setup() {
                given(productService.createProduct(any(ProductData.class)))
                        .will(invocation -> {
                            ProductData productData = invocation.getArgument(0);

                            System.out.println(productData.getName());

                            if (
                                    productData.getName().isEmpty()
                                            || productData.getMaker().isEmpty()
                            ) {
                                throw new MappingException("invalid product data");
                            }

                            return Product.builder()
                                    .id(1L)
                                    .name(productData.getName())
                                    .maker(productData.getMaker())
                                    .price(productData.getPrice())
                                    .build();
                        });
            }

            @Nested
            @DisplayName("주어진 상품의 정보가 올바르다면")
            class Context_with_correct_product_attributes {
                @Test
                @DisplayName("created 상태와 생성된 product 를 응답한다.")
                void It_response_created_and_created_product() throws Exception {
                    mockMvc.perform(
                            post("/products")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                            "\"price\":5000}")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isCreated())
                            .andExpect(content().string(containsString("쥐돌이")));

                    verify(productService).createProduct(any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("주어진 상품의 정보가 올바르지 않다면")
            class Context_without_correct_product_attributes {
                @Test
                @DisplayName("bad request 를 응답한다.")
                void It_respond_bad_request() throws Exception {
                    mockMvc.perform(
                            post("/products")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\":\"\",\"maker\":\"\"," +
                                            "\"price\":0}")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /products/{id} 요청은")
    class Describe_patch_products {
        @Nested
        @DisplayName("주어진 access token 이 올바를 때")
        class Context_with_valid_access_token {
            @BeforeEach
            void setup() {
                given(productService.updateProduct(eq(1L), any(ProductData.class)))
                        .will(invocation -> {
                            Long id = invocation.getArgument(0);
                            ProductData productData = invocation.getArgument(1);
                            return Product.builder()
                                    .id(id)
                                    .name(productData.getName())
                                    .maker(productData.getMaker())
                                    .price(productData.getPrice())
                                    .build();
                        });

                given(productService.updateProduct(eq(1000L), any(ProductData.class)))
                        .willThrow(new ProductNotFoundException(1000L));
            }

            @Nested
            @DisplayName("주어진 id 에 해당하는 product 가 존재할 때")
            class Context_when_exists_given_id_product {
                @Test
                @DisplayName("ok 와 변경된 product 를 응답한다.")
                void It_respond_ok_and_modified_product() throws Exception {
                    mockMvc.perform(
                            patch("/products/1")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                            "\"price\":5000}")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isOk())
                            .andExpect(content().string(containsString("쥐순이")));

                    verify(productService).updateProduct(eq(1L), any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("주어진 id 에 해당하는 product 가 존재하지 않을 때")
            class Context_when_not_exists_given_id_product {
                @Test
                @DisplayName("not found 를 응답한다.")
                void It_respond_not_found() throws Exception {
                    mockMvc.perform(
                            patch("/products/1000")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                            "\"price\":5000}")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isNotFound());

                    verify(productService).updateProduct(eq(1000L), any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("주어진 product 가 올바르지 않을 때")
            class Context_without_correct_product_attributes {
                @Test
                @DisplayName("bad request 를 응답한다.")
                void It_respond_bad_request() throws Exception {
                    mockMvc.perform(
                            patch("/products/1")
                                    .accept(MediaType.APPLICATION_JSON_UTF8)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\":\"\",\"maker\":\"\"," +
                                            "\"price\":0}")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isBadRequest());
                }
            }
        }

        @Nested
        @DisplayName("주어진 access token 이 올바르지 않을 때 때")
        class Context_with_invalid_access_token {
            @Test
            @DisplayName("unauthorized 를 응답한다.")
            void It_respond_unauthorized() throws Exception {
                mockMvc.perform(
                        patch("/products/1")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                                .header("Authorization", "Bearer " + INVALID_TOKEN)
                )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("주어진 access token 이 없을 때")
        class Context_without_access_token {
            @Test
            @DisplayName("unauthorized 를 응답한다.")
            void It_respond_unauthorized() throws Exception {
                mockMvc.perform(
                        patch("/products/1")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                )
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /products/{id} 요청은")
    class Describe_delete_products {
        @Nested
        @DisplayName("주어진 access token 이 올바를 때")
        class Context_with_valid_access_token {
            @BeforeEach
            void setup() {
                given(productService.deleteProduct(1000L))
                        .willThrow(new ProductNotFoundException(1000L));
            }

            @Nested
            @DisplayName("주어진 id 에 해당하는 상품이 존재할 때")
            class Context_when_exists_given_id_product {

                @Test
                @DisplayName("no content 를 응답한다.")
                void It_respond_no_content() throws Exception {
                    mockMvc.perform(
                            delete("/products/1")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isNoContent());

                    verify(productService).deleteProduct(1L);
                }
            }

            @Nested
            @DisplayName("주어진 id 에 해당하는 상품이 존재하지 않을  때")
            class Context_when_not_exists_given_id_product {
                @Test
                @DisplayName("not found 를 응답한다.")
                void It_respond_not_found() throws Exception {
                    mockMvc.perform(
                            delete("/products/1000")
                                    .header("Authorization", "Bearer " + VALID_TOKEN)
                    )
                            .andExpect(status().isNotFound());

                    verify(productService).deleteProduct(1000L);
                }
            }
        }

        @Nested
        @DisplayName("주어진 access token 이 올바르지 않을 때")
        class Context_with_invalid_access_token {
            @Test
            @DisplayName("unauthorized 를 응답한다.")
            void It_respond_unauthorized() throws Exception {
                mockMvc.perform(
                        patch("/products/1")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                                .header("Authorization", "Bearer " + INVALID_TOKEN)
                )
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}
