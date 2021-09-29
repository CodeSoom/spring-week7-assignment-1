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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private ProductData updatedProductDataFixture;
    private ProductData invalidProductDataFixture;

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

        updatedProductDataFixture = ProductData.builder()
                .name("mouse2")
                .maker("new balance")
                .price(5000)
                .build();

        invalidProductDataFixture = ProductData.builder()
                .name("mouse")
                .build();

        authorizationFixture = "Bearer 111.222.333";
    }

    @Nested
    @DisplayName("상품 생성 요청")
    class PostRequest {
        @DisplayName("생성된 상품정보와 201 Created HTTP 상태코드로 응답한다.")
        @Test
        void responsesWithCreatedProduct() throws Exception {
            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productDataFixture))
                            .header("Authorization", authorizationFixture)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.maker", is(productDataFixture.getMaker())))
                    .andExpect(jsonPath("$.price", is(productDataFixture.getPrice())))
                    .andExpect(jsonPath("$.name", is(productDataFixture.getName())));
        }

        @Nested
        @DisplayName("토큰이 없는 경우")
        class WithoutToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productDataFixture))
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 파라미터로 요청하면")
        class WithInvalidRequestBody {
            @DisplayName("400 Bad Request 에러로 응답한다.")
            @Test
            void responsesWithBadRequest() throws Exception {
                mockMvc.perform(post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidProductDataFixture))
                                .header("Authorization", authorizationFixture)
                        )
                        .andExpect(status().isBadRequest());
            }
        }
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

    @Nested
    @DisplayName("상품 수정 요청")
    class PatchRequest {
        private Product product;

        @BeforeEach
        void setupProduct() throws Exception {
            product = createProductBeforeTest(productDataFixture);
        }

        @DisplayName("수정된 상품과 200 Ok HTTP 상태코드로 응답한다.")
        @Test
        void responsesWithUpdatedProduct() throws Exception {
            mockMvc.perform(patch("/products/" + product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedProductDataFixture))
                            .header("Authorization", authorizationFixture))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is(updatedProductDataFixture.getName())));
        }

        @Nested
        @DisplayName("토큰이 없는 경우")
        class WithoutToken {
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            @Test
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(patch("/products/" + product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedProductDataFixture)))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 값으로 요청하면")
        class WithInvalidRequestBody {
            @DisplayName("400 Bad Request 에러로 응답한다.")
            @Test
            void responsesWithBadRequest() throws Exception {
                mockMvc.perform(patch("/products/" + product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidProductDataFixture))
                                .header("Authorization", authorizationFixture))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 상품이면")
        class WithNonExistentId {
            @BeforeEach
            void removeProduct() throws Exception {
                deleteProductBeforeTest(product.getId());
            }

            @DisplayName("404 NotFound 에러로 응답한다.")
            @Test
            void responsesWithNotFoundError() throws Exception {
                mockMvc.perform(patch("/products" + product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedProductDataFixture))
                                .header("Authorization", authorizationFixture))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("상품 삭제 요청")
    class DeleteRequest {
        private Product product;

        @BeforeEach
        void setupProduct() throws Exception {
            product = createProductBeforeTest(productDataFixture);
        }

        @Test
        @DisplayName("204 NoContent HTTP 상태코드로 응답한다")
        void responsesWithNoContentStatus() throws Exception {
            mockMvc.perform(delete("/products/" + product.getId())
                            .header("Authorization", authorizationFixture))
                    .andExpect(status().isNoContent());
        }

        @Nested
        @DisplayName("토큰이 없는 경우")
        class WithoutToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(delete("/products/" + product.getId()))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 상품이면")
        class WithNonExistentId {
            @BeforeEach
            void removeProduct() throws Exception {
                deleteProductBeforeTest(product.getId());
            }

            @Test
            @DisplayName("404 NotFound 에러로 응답한다.")
            void responsesWithNotFoundError() throws Exception {
                mockMvc.perform(delete("/products/" + product.getId())
                                .header("Authorization", authorizationFixture))
                        .andExpect(status().isNotFound());
            }
        }
    }
}
