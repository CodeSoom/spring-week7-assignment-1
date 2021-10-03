package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TestUtils;
import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String authorizationFixture;
    private ProductData productDataFixture;
    private Product createProductBeforeTest(ProductData productData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/products")
                .content(objectMapper.writeValueAsString(productData))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizationFixture)
        );

        return TestUtils.content(actions, Product.class);
    }

    private void deleteProductBeforeTest(Long id) throws Exception {
        mockMvc.perform(delete("/products/" + id)
                .header("Authorization", authorizationFixture));
    }

    @BeforeEach
    void mockParseValidToken() {
        given(authenticationService.parseToken(any(String.class))).willReturn(1L);
    }

    @BeforeEach
    void setupFixtures() {
        productDataFixture = ProductData.builder()
                .name("mouse")
                .maker("adidas")
                .price(5000)
                .build();

        authorizationFixture = "Bearer 111.222.333";
    }

    @Nested
    @DisplayName("상품 목록 조회 요청")
    class GetListRequest {
        private Product product;

        @BeforeEach
        void setupProduct() throws Exception {
            product = createProductBeforeTest(productDataFixture);
        }

        @Test
        @DisplayName("상품 목록과 200 Ok HTTP 상태코드로 응답한다.")
        void responsesWithProducts() throws Exception {
            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(product.getName())));
        }
    }

    @Nested
    @DisplayName("상품 상세 조회 요청")
    class GetDetailsRequest {
        private Product product;

        @BeforeEach
        void setupProduct() throws Exception {
            product = createProductBeforeTest(productDataFixture);
        }

        @Test
        @DisplayName("상품 상세와 200 Ok HTTP 상태코드로 응답한다.")
        void responsesWithProducts() throws Exception {
            mockMvc.perform(get("/products/" + product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maker", is(product.getMaker())))
                    .andExpect(jsonPath("$.price", is(product.getPrice())))
                    .andExpect(jsonPath("$.name", is(product.getName())));
        }

        @Nested
        @DisplayName("해당 식별자의 상품을 찾을 수 없는 경우")
        class WithNotFoundProduct {
            @BeforeEach
            void removeProduct() throws Exception {
                deleteProductBeforeTest(product.getId());
            }

            @Test
            @DisplayName("404 Bad Request 에러로 응답한다.")
            void responsesWithNotFoundError() throws Exception {
                mockMvc.perform(get("/products/"+ product.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }
}
