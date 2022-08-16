package com.codesoom.assignment.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController 클래스의")
public class ProductControllerTest {
    public static final String QUANTITY = "10";
    public static final String PRICE = "10000";
    public static final String USER_PATH = "/users";
    public static final String PRODUCT_PATH = "/products";
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String USER_NAME = "박범진";
    public static final String PRODUCT_NAME = "상품명";
    private static final Map<String, String> USER_DATA = Map.of(
            "email", EMAIL,
            "password", PASSWORD,
            "name", USER_NAME
    );

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> productData(String userId) {
        return Map.of(
                "userId", userId,
                "name", PRODUCT_NAME,
                "quantity", QUANTITY,
                "price", PRICE
        );
    }

    private Map<String, String> create(Map<String, String> data, String path) throws Exception {
        return objectMapper.readValue(mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<>(){});
    }

    @Nested
    @DisplayName("register 메서드는")
    class Describe_register {
        @Nested
        @DisplayName("상품, 유저 정보가 주어지면")
        class Context_userData {
            @Test
            @DisplayName("상품을 생성하고 상품 조회 정보와 201을 응답한다")
            void It_returns_productInfo() throws Exception {
                Map<String, String> createdUser = create(USER_DATA, USER_PATH);

                mockMvc.perform(post(PRODUCT_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productData(createdUser.get("id")))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name", Is.is(PRODUCT_NAME)))
                        .andExpect(jsonPath("$.quantity", Is.is(QUANTITY)))
                        .andExpect(jsonPath("$.price", Is.is(PRICE)));
            }
        }
    }
}
