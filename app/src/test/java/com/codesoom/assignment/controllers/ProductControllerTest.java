package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller의 ")
class ProductControllerTest {

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = VALID_TOKEN + "wrong";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("list 메소드는 ")
    class Describe_getProductList {

        @Nested
        @DisplayName("상품 리스트가 없으면")
        class Context_with_no_product {
            private List<Product> productList = new ArrayList<>();

            @BeforeEach
            void setUp() {
                given(productService.getProducts()).willReturn(productList);
            }

            @Test
            @DisplayName("상품 빈 리스트와 상태코드 200을 응답한다.")
            void list() throws Exception {
                mvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"))
                        .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                        .andExpect(jsonPath("$.*", hasSize(productList.size())));
            }
        }

        @Nested
        @DisplayName("상품 리스트가 있으면")
        class Context_with_product {
            private List<Product> productList = new ArrayList<>();

            @BeforeEach
            void setUp() {
                Product product = Product.builder()
                        .id(1L)
                        .name("쥐돌이")
                        .maker("냥이월드")
                        .price(5000)
                        .build();

                Product product2 = Product.builder()
                        .id(2L)
                        .name("쥐돌이2")
                        .maker("냥이월드2")
                        .price(10000)
                        .build();

                productList.add(product);
                productList.add(product2);
                given(productService.getProducts()).willReturn(productList);
            }

            @Test
            @DisplayName("상품 리스트와 상태코드 200을 응답한다.")
            void list() throws Exception {
                mvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                        .andExpect(jsonPath("$.*", hasSize(productList.size())));
            }
        }
    }

    @Nested
    @DisplayName("detail 메소드는 ")
    class Describe_getProductDetails {

        @Nested
        @DisplayName("상품이 없으면")
        class Context_without_product {

            @BeforeEach
            void setUp() {
                given(productService.getProduct(1L)).willThrow(new ProductNotFoundException(1L));
            }

            @Test
            @DisplayName("예외를 던지고 상태코드 404를 응답한다.")
            void getDetailsWithoutProduct() throws Exception {
                mvc.perform(get("/products/1")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("상품이 있으면 ")
        class Context_with_product {
            @BeforeEach
            void setUp() {
                Product product = Product.builder()
                        .id(1L)
                        .name("쥐돌이")
                        .maker("냥이월드")
                        .price(5000)
                        .build();

                given(productService.getProduct(1L)).willReturn(product);
            }

            @Test
            @DisplayName("상품 정보와 상태코드 200을 응답한다.")
            void getDetailsWithProduct() throws Exception {
                mvc.perform(get("/products/1")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1L))
                        .andExpect(jsonPath("$.name").value("쥐돌이"))
                        .andExpect(jsonPath("$.maker").value("냥이월드"))
                        .andExpect(jsonPath("$.price").value(5000));
            }
        }
    }

    @Nested
    @DisplayName("create 메소드는")
    class Describe_createProduct {

        @Nested
        @DisplayName("인증이 되지 않은 경우")
        class Context_with_Not_Authentication {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(INVALID_TOKEN))
                        .willThrow(new InvalidTokenException(INVALID_TOKEN));
            }

            @Test
            @DisplayName("토큰이 유효하지 않으면 상태코드 401을 응답한다.")
            void createProductWithoutAuthentication() throws Exception {
                mvc.perform(
                        post("/products")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                        "\"price\":5000}")
                                .header("Authorization", "Bearer " + INVALID_TOKEN)
                )
                        .andDo(print())
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("인증이 된 경우")
        class Context_with_Authentication {

            @BeforeEach
            void setUp() {
                Product product = Product.builder()
                        .id(1L)
                        .name("쥐돌이")
                        .maker("냥이월드")
                        .price(5000)
                        .build();

                given(productService.createProduct(any(ProductData.class))).willReturn(product);
                given(authenticationService.parseToken(VALID_TOKEN)).willReturn(1L);

            }

            @Test
            @DisplayName("전달 값이 유효하면 상품을 등록하고 상태코드 201을 응답한다.")
            void createProductWithAuthentication() throws Exception {
                mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                "\"price\":5000}")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                        .andDo(print())
                        .andExpect(status().isCreated());

                verify(productService).createProduct(any(ProductData.class));
            }

            @Test
            @DisplayName("전달 값이 유효하지 않으면 상태코드 401을 응답한다.")
            void createProductWithAuthenticationWithWrongAttributes() throws Exception {
                mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"maker\":\"\"," +
                                "\"price\":5000}")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Describe_updateProduct {

        @Nested
        @DisplayName("인증이 된 경우")
        class Context_with_Authentication {

            @Test
            @DisplayName("전달 값이 유효하고 상품이 존재하면 상품을 수정하고 200을 응답한다.")
            void updateProductWithExistedProduct() {

            }

            @Test
            @DisplayName("상품을 찾을 수 없으면 400을 응답한다.")
            void updateProductWithNotExistedProduct() {

            }

            @Test
            @DisplayName("전달 값이 유효하지 않으면 400을 응답한다.")
            void updateProductWithWrongAttributes() {

            }
        }

        @Nested
        @DisplayName("인증이 되지 않은 경우")
        class Context_without_Authentication {

            @Test
            @DisplayName("토큰이 유효하지 않으면 상태코드 401을 응답한다.")
            void updateWithInvalidToken() {

            }
        }
    }

    @Nested
    @DisplayName("delete 메소드는")
    class Describe_deleteProduct {

        @Nested
        @DisplayName("인증이 된 경우")
        class Context_with_Authentication {

            @Test
            @DisplayName("전달 값이 유효하고 상품이 존재하면 상품을 삭제하고 200을 응답한다.")
            void deleteProductWithExistedProduct() {

            }

            @Test
            @DisplayName("상품을 찾을 수 없으면 400을 응답한다.")
            void deleteProductWithNotExistedProduct() {

            }
        }

        @Nested
        @DisplayName("인증이 되지 않은 경우")
        class Context_without_Authentication {

            @Test
            @DisplayName("토큰이 유효하지 않으면 상태코드 401을 응답한다.")
            void deleteWithInvalidToken() {

            }
        }
    }
}
