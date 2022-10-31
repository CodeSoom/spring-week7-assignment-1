package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.ProductDto.RegisterParam;
import com.codesoom.assignment.security.JwtTokenProvider;
import com.codesoom.assignment.utils.ProductSampleFactory;
import com.codesoom.assignment.utils.UserSampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.codesoom.assignment.utils.ProductSampleFactory.FieldName.MAKER;
import static com.codesoom.assignment.utils.ProductSampleFactory.FieldName.NAME;
import static com.codesoom.assignment.utils.ProductSampleFactory.FieldName.PRICE;
import static com.codesoom.assignment.utils.ProductSampleFactory.ValueType.EMPTY;
import static com.codesoom.assignment.utils.ProductSampleFactory.ValueType.NULL;
import static com.codesoom.assignment.utils.ProductSampleFactory.ValueType.WHITESPACE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController 클래스")
class ProductControllerTest {
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        // ResponseBody JSON에 한글이 깨지는 문제로 추가
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Nested
    @DisplayName("list[/products::GET] 메소드는")
    class Describe_list {
        ResultActions subject() throws Exception {
            return mockMvc.perform(get("/products"));
        }

        @Nested
        @DisplayName("상품이 존재하면")
        class Context_with_products {
            private List<Product> givenProducts = new ArrayList<>();

            @BeforeEach
            void prepare() {
                productRepository.save(ProductSampleFactory.createProduct());
                productRepository.save(ProductSampleFactory.createProduct());

                givenProducts = productRepository.findAll();
            }

            @Test
            @DisplayName("OK(200)와 모든 상품을 리턴한다")
            void it_returns_200_and_all_products() throws Exception {
                final ResultActions resultActions = subject();

                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].name").value(equalTo(givenProducts.get(0).getName())))
                        .andExpect(jsonPath("$[1].name").value(equalTo(givenProducts.get(1).getName())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("상품이 존재하지 않으면")
        class Context_with_empty_db {
            @BeforeEach
            void prepare() {
                List<Product> products = productRepository.findAll();

                products.forEach(product -> productRepository.delete(product));
            }

            @Test
            @DisplayName("OK(200)와 빈 데이터를 리턴한다")
            void it_return_200_and_empty_array() throws Exception {
                final ResultActions resultActions = subject();

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("[]"))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("detail[/products/id::GET] 메소드는")
    class Describe_detail {
        ResultActions subject(Long id) throws Exception {
            return mockMvc.perform(get("/products/{id}", id));
        }

        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id {
            private final Product givenProduct = productRepository.save(ProductSampleFactory.createProduct());
            private final Long PRODUCT_ID = givenProduct.getId();

            @Test
            @DisplayName("OK(200)와 요청한 상품을 리턴한다")
            void it_returns_200_and_searched_product() throws Exception {
                final ResultActions resultActions = subject(PRODUCT_ID);

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("name").value(equalTo(givenProduct.getName())))
                        .andExpect(jsonPath("maker").value(equalTo(givenProduct.getMaker())))
                        .andDo(print());


            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id {
            private final Long INVALID_PRODUCT_ID = 9999L;

            @Test
            @DisplayName("NOT_FOUND(404)와 예외 메시지를 리턴한다")
            void it_returns_404_and_message() throws Exception {
                final ResultActions resultActions = subject(INVALID_PRODUCT_ID);

                resultActions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("message", containsString("Product not found")))
                        .andDo(print());

            }
        }
    }

    @Nested
    @DisplayName("createProduct[/products::POST] 메소드는")
    class Describe_createProduct {
        private final User givenUser = userRepository.save(UserSampleFactory.createUser());
        private final Long EXIST_USER_ID = givenUser.getId();
        private final String VALID_TOKEN = jwtTokenProvider.createToken(EXIST_USER_ID);

        ResultActions subject(RegisterParam request, String accessToken) throws Exception {
            return mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", "Bearer " + accessToken));
        }

        @Nested
        @DisplayName("새로운 상품을 등록하면")
        class Context_with_valid_token_and_new_product {
            private final RegisterParam givenRequest = ProductSampleFactory.createRequestParam();

            @Test
            @DisplayName("CREATED(201)와 등록된 상품을 리턴한다.")
            void it_returns_201_and_registered_product() throws Exception {
                final ResultActions resultActions = subject(givenRequest, VALID_TOKEN);

                resultActions.andExpect(status().isCreated())
                        .andExpect(jsonPath("name").value(equalTo(givenRequest.getName())))
                        .andExpect(jsonPath("maker").value(equalTo(givenRequest.getMaker())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("필수항목에 빈 값이 주어지면")
        class Context_with_blank_value {
            private final List<RegisterParam> givenProudcts = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, NULL));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, EMPTY));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, WHITESPACE));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, NULL));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, EMPTY));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, WHITESPACE));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(PRICE, NULL));
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다.")
            void it_returns_400_and_error_message() throws Exception {
                givenProudcts.forEach(this::test);
            }

            private void test(RegisterParam givenProduct) {
                try {
                    ResultActions resultActions = subject(givenProduct, VALID_TOKEN);

                    resultActions.andExpect(status().isBadRequest())
                            .andDo(print());
                } catch (Exception e) {
                }
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_empty_token {
            private final RegisterParam givenRequest = ProductSampleFactory.createRequestParam();

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                ResultActions resultActions = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)));

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("잘못된 토큰이 주어지면")
        class Context_with_wrong_token {
            private final RegisterParam givenRequest = ProductSampleFactory.createRequestParam();

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                ResultActions resultActions = subject(givenRequest, INVALID_TOKEN);

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("updateProduct[/products/id::PATCH] 메소드는")
    class Describe_updateProduct {
        private final User savedUser = userRepository.save(UserSampleFactory.createUser());
        private final Product savedProduct = productRepository.save(ProductSampleFactory.createProduct());
        private final Long EXIST_USER_ID = savedUser.getId();
        private final Long PRODUCT_ID = savedProduct.getId();
        private final String VALID_TOKEN = jwtTokenProvider.createToken(EXIST_USER_ID);

        ResultActions subject(Long id, RegisterParam request, String accessToken) throws Exception {
            return mockMvc.perform(patch("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", "Bearer " + accessToken));
        }

        @Nested
        @DisplayName("수정된 상품정보가 주어지면")
        class Context_with_modified_product_info {
            private final RegisterParam givenRequest = new RegisterParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedProduct.getName());
                givenRequest.setMaker("수정_" + savedProduct.getMaker());
                givenRequest.setPrice(savedProduct.getPrice() + 5000);
            }

            @Test
            @DisplayName("OK(200)와 수정된 상품을 리턴한다.")
            void it_returns_200_and_modified_product() throws Exception {
                final ResultActions resultActions = subject(PRODUCT_ID, givenRequest, VALID_TOKEN);

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("name").value(equalTo(givenRequest.getName())))
                        .andExpect(jsonPath("maker").value(equalTo(givenRequest.getMaker())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("필수항목에 빈 값이 주어지면")
        class Context_with_blank_value {
            private final List<RegisterParam> givenProudcts = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, NULL));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, EMPTY));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(NAME, WHITESPACE));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, NULL));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, EMPTY));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(MAKER, WHITESPACE));
                givenProudcts.add(ProductSampleFactory.createRequestParamWith(PRICE, NULL));
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다.")
            void it_returns_400_and_error_message() throws Exception {
                givenProudcts.forEach(this::test);
            }

            private void test(RegisterParam givenProduct) {
                try {
                    ResultActions resultActions = subject(PRODUCT_ID, givenProduct, VALID_TOKEN);

                    resultActions.andExpect(status().isBadRequest())
                            .andDo(print());
                } catch (Exception e) {
                }
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_empty_token {
            private final RegisterParam givenRequest = new RegisterParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedProduct.getName());
                givenRequest.setMaker("수정_" + savedProduct.getMaker());
                givenRequest.setPrice(savedProduct.getPrice() + 5000);
            }

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                ResultActions resultActions = mockMvc.perform(patch("/products/{id}", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)));

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("잘못된 토큰이 주어지면")
        class Context_with_wrong_token {
            private final RegisterParam givenRequest = new RegisterParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedProduct.getName());
                givenRequest.setMaker("수정_" + savedProduct.getMaker());
                givenRequest.setPrice(savedProduct.getPrice() + 5000);
            }

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                ResultActions resultActions = subject(PRODUCT_ID, givenRequest, INVALID_TOKEN);

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("유효하지않은 상품ID가 주어지면")
        class Context_with_invalid_product_id {
            private final Long INVALID_PRODUCT_ID = 9999L;
            private final RegisterParam givenRequest = ProductSampleFactory.createRequestParam();

            @Test
            @DisplayName("NOT_FOUND(404)와 예외 메시지를 리턴한다")
            void it_returns_404_and_message() throws Exception {
                ResultActions resultActions = subject(INVALID_PRODUCT_ID, givenRequest, VALID_TOKEN);

                resultActions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("message", containsString("Product not found")))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("deleteProduct[/products/id::DELETE] 메소드는")
    class Describe_deleteProduct {
        private final User givenUser = userRepository.save(UserSampleFactory.createUser());
        private final Product savedProduct = productRepository.save(ProductSampleFactory.createProduct());
        private final Long EXIST_USER_ID = givenUser.getId();
        private final Long PRODUCT_ID = savedProduct.getId();
        private final String VALID_TOKEN = jwtTokenProvider.createToken(EXIST_USER_ID);

        ResultActions subject(Long id, String accessToken) throws Exception {
            return mockMvc.perform(delete("/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken));
        }

        @Nested
        @DisplayName("유효한 상품ID가 주어지면")
        class Context_with_valid_id {
            @Test
            @DisplayName("상품을 삭제하고 NO_CONTENT(204)를 리턴한다")
            void it_returns_204() throws Exception {
                final ResultActions resultActions = subject(PRODUCT_ID, VALID_TOKEN);

                resultActions.andExpect(status().isNoContent())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_empty_token {
            @Test
            @DisplayName("UNAUTHORIZED(401)를 리턴한다")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = mockMvc.perform(delete("/products/" + PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON));

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("잘못된 토근이 주어지면")
        class Context_with_wrong_token {
            @Test
            @DisplayName("UNAUTHORIZED(401)를 리턴한다")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = subject(PRODUCT_ID, INVALID_TOKEN);

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id {
            private final Long INVALID_PRODUCT_ID = 9999L;

            @Test
            @DisplayName("NOT_FOUND(404)와 예외 메시지를 리턴한다")
            void it_returns_404_and_message() throws Exception {
                final ResultActions resultActions = subject(INVALID_PRODUCT_ID, VALID_TOKEN);

                resultActions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("message", containsString("Product not found")))
                        .andDo(print());
            }
        }
    }
}
