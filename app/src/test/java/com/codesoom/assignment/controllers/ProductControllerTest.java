package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController의 ")
class ProductControllerTest {

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
        @DisplayName("장난감 리스트가 없으면")
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
        @DisplayName("장난감 리스트가 있으면")
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
        @DisplayName("인증이 되지 않으면")
        class Context_with_Not_Authentication {

            @Test
            @WithAnonymousUser
            @DisplayName("상태코드 401을 응답한다.")
            void createProductWithoutAuthentication() throws Exception {
                mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"쥐돌이\",\"maker\":\"냥이월드\"," +
                                "\"price\":5000}"))
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}
