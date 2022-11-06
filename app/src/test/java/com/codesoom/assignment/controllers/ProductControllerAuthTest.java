package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.infra.JpaProductRepository;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductController 인증 테스트")
class ProductControllerAuthTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaProductRepository productRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @AfterEach
    void clear() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("상품 단순 조회 요청 메소드는")
    class Describe_list_and_detail {

        @Nested
        @DisplayName("요청자 인증이 주어지지 않더라도")
        class Context_with_not_authenticated {
            private Long id;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(Product.builder()
                        .name("CAT TOY")
                        .maker("SHOP")
                        .price(10000)
                        .build());
                id = product.getId();
            }

            @Test
            @DisplayName("200 응답을 생성한다")
            void it_creates_ok() throws Exception {
                mockMvc.perform(get("/products"))
                        .andExpect(status().isOk());

                mockMvc.perform(get("/products/{id}", id))
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("상품 생성 요청 메소드는")
    class Describe_create {

        @Nested
        @DisplayName("Authorization 헤더가 주어지지 않으면")
        class Context_without_token {

            @Test
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized() throws Exception {
                mockMvc.perform(post("/products")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰 정보가 주어지면")
        class Context_with_wrong_token {

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "a.b.c", "1.2.3", "hello.world"})
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized(String token) throws Exception {
                mockMvc.perform(post("/products")
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효한 토큰 정보가 주어지면")
        class Context_with_token {
            private String token;

            @BeforeEach
            void setUp() {
                User user = userRepository.save(getUser());
                Long userId = user.getId();
                token = jwtUtil.encode(userId);
            }

            @Test
            @DisplayName("201 응답을 생성한다")
            void it_creates_created() throws Exception {
                mockMvc.perform(post("/products")
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isCreated());
            }
        }
    }
    
    @Nested
    @DisplayName("상품 수정 요청 메소드는")
    class Describe_update {

        @Nested
        @DisplayName("Authorization 헤더가 주어지지 않으면")
        class Context_without_token {
            private Long id;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();
            }

            @Test
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized() throws Exception {
                mockMvc.perform(patch("/products/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰 정보가 주어지면")
        class Context_with_wrong_token {
            private Long id;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "a.b.c", "1.2.3", "hello.world"})
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized(String token) throws Exception {
                mockMvc.perform(patch("/products/{id}", id)
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효한 토큰 정보가 주어지면")
        class Context_with_token {
            private Long id;
            private String token;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();

                User user = userRepository.save(getUser());
                Long userId = user.getId();
                token = jwtUtil.encode(userId);
            }

            @Test
            @DisplayName("200 응답을 생성한다")
            void it_creates_ok() throws Exception {
                mockMvc.perform(patch("/products/{id}", id)
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isOk());
            }
        }
    }
    
    @Nested
    @DisplayName("상품 삭제 요청 메소드는")
    class Describe_destroy {

        @Nested
        @DisplayName("Authorization 헤더가 주어지지 않으면")
        class Context_without_token {
            private Long id;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();
            }

            @Test
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized() throws Exception {
                mockMvc.perform(delete("/products/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰 정보가 주어지면")
        class Context_with_wrong_token {
            private Long id;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "a.b.c", "1.2.3", "hello.world"})
            @DisplayName("401 응답을 생성한다")
            void it_creates_unauthorized(String token) throws Exception {
                mockMvc.perform(delete("/products/{id}", id)
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효한 토큰 정보가 주어지면")
        class Context_with_token {
            private Long id;
            private String token;

            @BeforeEach
            void setUp() {
                Product product = productRepository.save(getProduct());
                id = product.getId();

                User user = userRepository.save(getUser());
                Long userId = user.getId();
                token = jwtUtil.encode(userId);
            }

            @Test
            @DisplayName("204 응답을 생성한다")
            void it_creates_no_content() throws Exception {
                mockMvc.perform(delete("/products/{id}", id)
                                .header(AUTHORIZATION_HEADER, BEARER + token)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getProductRequest())))
                        .andExpect(status().isNoContent());
            }
        }
    }

    private ProductData getProductRequest() {
        return ProductData.builder()
                .name("NEW CAT TOY")
                .maker("NEW SHOP")
                .price(15000)
                .build();
    }

    private Product getProduct() {
        return Product.builder()
                .name("CAT TOY")
                .maker("SHOP")
                .price(10000)
                .build();
    }

    private User getUser() {
        return User.builder()
                .name("TESTER")
                .email("TESTER@example.com")
                .password("TEST1234")
                .build();
    }
}
