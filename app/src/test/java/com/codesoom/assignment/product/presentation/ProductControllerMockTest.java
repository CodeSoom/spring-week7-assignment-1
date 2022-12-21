package com.codesoom.assignment.product.presentation;

import com.codesoom.assignment.MockMvcCharacterEncodingCustomizer;
import com.codesoom.assignment.common.utils.JsonUtil;
import com.codesoom.assignment.product.application.ProductService;
import com.codesoom.assignment.product.application.exception.ProductNotFoundException;
import com.codesoom.assignment.product.domain.Product;
import com.codesoom.assignment.product.presentation.dto.ProductData;
import com.codesoom.assignment.session.application.AuthenticationService;
import com.codesoom.assignment.session.application.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.codesoom.assignment.support.AuthHeaderFixture.INVALID_VALUE_TOKEN_1;
import static com.codesoom.assignment.support.AuthHeaderFixture.VALID_TOKEN_1;
import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.IdFixture.ID_MIN;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_1;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_2;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_INVALID_MAKER;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_INVALID_NAME;
import static com.codesoom.assignment.support.ProductFixture.PRODUCT_INVALID_PRICE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProductController.class, MockMvcCharacterEncodingCustomizer.class})
@DisplayName("ProductController 웹 유닛 테스트")
class ProductControllerMockTest {
    private static final String REQUEST_PRODUCT_URL = "/products";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(authenticationService.parseToken(eq(VALID_TOKEN_1.토큰_값())))
                .willReturn(VALID_TOKEN_1.아이디());

        given(authenticationService.parseToken(eq(INVALID_VALUE_TOKEN_1.토큰_값())))
                .willThrow(new InvalidTokenException(INVALID_VALUE_TOKEN_1.토큰_값()));
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class list_메서드는 {

        @Test
        @DisplayName("200 코드로 응답한다")
        void it_responses_200() throws Exception {
            ResultActions perform = mockMvc.perform(
                    get("/products")
            );

            perform.andExpect(status().isOk());

            verify(productService).getProducts();
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class detail_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {

            @BeforeEach
            void setUp() {
                given(productService.getProduct(ID_MIN.value()))
                        .willReturn(PRODUCT_1.엔티티_생성(ID_MIN.value()));
            }

            @Test
            @DisplayName("200 코드로 응답한다")
            void it_responses_200() throws Exception {
                ResultActions perform = mockMvc.perform(
                        get("/products/" + ID_MIN.value())
                );

                perform.andExpect(status().isOk());

                verify(productService).getProduct(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {

            @BeforeEach
            void setUp() {
                given(productService.getProduct(ID_MAX.value()))
                        .willThrow(new ProductNotFoundException(ID_MAX.value()));
            }

            @Test
            @DisplayName("404 코드로 응답한다")
            void it_responses_404() throws Exception {
                ResultActions perform = mockMvc.perform(
                        get("/products/" + ID_MAX.value())
                );

                perform.andExpect(status().isNotFound());

                verify(productService).getProduct(ID_MAX.value());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class create_메서드는 {

        @Nested
        @DisplayName("유효한 상품 정보가 주어지면")
        class Context_with_valid_product {

            @BeforeEach
            void setUp() {
                given(productService.createProduct(any(ProductData.class)))
                        .will(invocation -> {
                            ProductData product = invocation.getArgument(0);

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
            @DisplayName("201 코드로 응답한다")
            void it_responses_201() throws Exception {
                ResultActions perform = 상품_등록_API_요청(
                        VALID_TOKEN_1.인증_헤더값(),
                        PRODUCT_1.등록_요청_데이터_생성()
                );

                perform.andExpect(status().isCreated());

                verify(productService).createProduct(any(ProductData.class));
            }
        }

        @Nested
        @DisplayName("유효하지 않은 상품 정보가 주어지면")
        class Context_with_invalid_product {

            @Nested
            @DisplayName("상품명이 공백일 경우")
            class Context_with_empty_name {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 상품_등록_API_요청(
                            VALID_TOKEN_1.인증_헤더값(),
                            PRODUCT_INVALID_NAME.등록_요청_데이터_생성()
                    );

                    perform.andExpect(status().isBadRequest());

                    verify(productService, never()).createProduct(any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("메이커가 공백일 경우")
            class Context_with_empty_maker {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 상품_등록_API_요청(
                            VALID_TOKEN_1.인증_헤더값(),
                            PRODUCT_INVALID_MAKER.등록_요청_데이터_생성()
                    );

                    perform.andExpect(status().isBadRequest());

                    verify(productService, never()).createProduct(any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("가격이 0원 미만일 경우")
            class Context_with_negative_price {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 상품_등록_API_요청(
                            VALID_TOKEN_1.인증_헤더값(),
                            PRODUCT_INVALID_PRICE.등록_요청_데이터_생성()
                    );

                    perform.andExpect(status().isBadRequest());

                    verify(productService, never()).createProduct(any(ProductData.class));
                }
            }
        }

        @Nested
        @DisplayName("유효하지 않은 인증 토큰이 주어지면")
        class Context_with_invalid_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = 상품_등록_API_요청(
                        INVALID_VALUE_TOKEN_1.인증_헤더값(),
                        PRODUCT_2.등록_요청_데이터_생성()
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).createProduct(any(ProductData.class));
            }
        }

        @Nested
        @DisplayName("인증 토큰이 없다면")
        class Context_with_not_exist_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = mockMvc.perform(
                        post(REQUEST_PRODUCT_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(PRODUCT_2.등록_요청_데이터_생성()))
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).createProduct(any(ProductData.class));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class update_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {

            @Nested
            @DisplayName("유효한 상품 정보가 주어지면")
            class Context_with_valid_product {

                @BeforeEach
                void setUp() {
                    given(productService.updateProduct(eq(ID_MIN.value()), any(ProductData.class)))
                            .will(invocation -> {
                                Long productId = invocation.getArgument(0);
                                ProductData product = invocation.getArgument(1);

                                return Product.builder()
                                        .id(productId)
                                        .name(product.getName())
                                        .maker(product.getMaker())
                                        .price(product.getPrice())
                                        .imageUrl(product.getImageUrl())
                                        .build();
                            });
                }

                @Test
                @DisplayName("200 코드로 응답한다")
                void it_responses_200() throws Exception {
                    ResultActions perform = 상품_수정_API_요청(
                            ID_MIN.value(),
                            VALID_TOKEN_1.인증_헤더값(),
                            PRODUCT_1.수정_요청_데이터_생성()
                    );

                    perform.andExpect(status().isOk());

                    verify(productService).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
                }
            }

            @Nested
            @DisplayName("유효하지 않은 상품 정보가 주어지면")
            class Context_with_invalid_product {

                @Nested
                @DisplayName("상품명이 공백일 경우")
                class Context_with_empty_name {

                    @Test
                    @DisplayName("400 코드로 응답한다")
                    void it_responses_400() throws Exception {
                        ResultActions perform = 상품_수정_API_요청(
                                ID_MIN.value(),
                                VALID_TOKEN_1.인증_헤더값(),
                                PRODUCT_INVALID_NAME.수정_요청_데이터_생성()
                        );

                        perform.andExpect(status().isBadRequest());

                        verify(productService, never()).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
                    }
                }

                @Nested
                @DisplayName("메이커가 공백일 경우")
                class Context_with_empty_maker {

                    @Test
                    @DisplayName("400 코드로 응답한다")
                    void it_responses_400() throws Exception {
                        ResultActions perform = 상품_수정_API_요청(
                                ID_MIN.value(),
                                VALID_TOKEN_1.인증_헤더값(),
                                PRODUCT_INVALID_MAKER.수정_요청_데이터_생성()
                        );

                        perform.andExpect(status().isBadRequest());

                        verify(productService, never()).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
                    }
                }

                @Nested
                @DisplayName("가격이 0원 미만일 경우")
                class Context_with_negative_price {

                    @Test
                    @DisplayName("400 코드로 응답한다")
                    void it_responses_400() throws Exception {
                        ResultActions perform = 상품_수정_API_요청(
                                ID_MIN.value(),
                                VALID_TOKEN_1.인증_헤더값(),
                                PRODUCT_INVALID_PRICE.수정_요청_데이터_생성()
                        );

                        perform.andExpect(status().isBadRequest());

                        verify(productService, never()).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
                    }
                }
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {

            @BeforeEach
            void setUp() {
                given(productService.updateProduct(eq(ID_MAX.value()), any(ProductData.class)))
                        .willThrow(new ProductNotFoundException(ID_MAX.value()));
            }

            @Test
            @DisplayName("404 코드로 응답한다")
            void it_responses_404() throws Exception {
                ResultActions perform = 상품_수정_API_요청(
                        ID_MAX.value(),
                        VALID_TOKEN_1.인증_헤더값(),
                        PRODUCT_2.수정_요청_데이터_생성()
                );

                perform.andExpect(status().isNotFound());

                verify(productService).updateProduct(eq(ID_MAX.value()), any(ProductData.class));
            }
        }

        @Nested
        @DisplayName("유효하지 않은 인증 토큰이 주어지면")
        class Context_with_invalid_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = 상품_수정_API_요청(
                        ID_MIN.value(),
                        INVALID_VALUE_TOKEN_1.인증_헤더값(),
                        PRODUCT_1.수정_요청_데이터_생성()
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
            }
        }

        @Nested
        @DisplayName("인증 토큰이 없다면")
        class Context_with_not_exist_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = mockMvc.perform(
                        patch(REQUEST_PRODUCT_URL + "/" + ID_MIN.value())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(PRODUCT_1.수정_요청_데이터_생성()))
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).updateProduct(eq(ID_MIN.value()), any(ProductData.class));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class delete_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {

            @Test
            @DisplayName("204 코드로 응답한다")
            void it_responses_204() throws Exception {
                ResultActions perform = 상품_삭제_API_요청(
                        ID_MIN.value(),
                        VALID_TOKEN_1.인증_헤더값()
                );

                perform.andExpect(status().isNoContent());

                verify(productService).deleteProduct(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {

            @BeforeEach
            void setUp() {
                given(productService.deleteProduct(ID_MAX.value()))
                        .willThrow(new ProductNotFoundException(ID_MAX.value()));
            }

            @Test
            @DisplayName("404 코드로 응답한다")
            void it_responses_404() throws Exception {
                ResultActions perform = 상품_삭제_API_요청(
                        ID_MAX.value(),
                        VALID_TOKEN_1.인증_헤더값()
                );

                perform.andExpect(status().isNotFound());

                verify(productService).deleteProduct(ID_MAX.value());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 인증 토큰이 주어지면")
        class Context_with_invalid_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = 상품_삭제_API_요청(
                        ID_MIN.value(),
                        INVALID_VALUE_TOKEN_1.인증_헤더값()
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).deleteProduct(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("인증 토큰이 없다면")
        class Context_with_not_exist_token {
            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = mockMvc.perform(
                        delete(REQUEST_PRODUCT_URL + "/" + ID_MIN.value())
                );

                perform.andExpect(status().isUnauthorized());

                verify(productService, never()).deleteProduct(ID_MIN.value());
            }
        }
    }

    private ResultActions 상품_등록_API_요청(String authHeader,
                                       ProductData requestData) throws Exception {
        return mockMvc.perform(
                post(REQUEST_PRODUCT_URL)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValueAsString(requestData))
        );
    }

    private ResultActions 상품_수정_API_요청(Long productId,
                                       String authHeader,
                                       ProductData requestData) throws Exception {
        return mockMvc.perform(
                patch(REQUEST_PRODUCT_URL + "/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValueAsString(requestData))
        );
    }

    private ResultActions 상품_삭제_API_요청(Long productId, String authHeader) throws Exception {
        return mockMvc.perform(
                delete(REQUEST_PRODUCT_URL + "/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
        );
    }
}
