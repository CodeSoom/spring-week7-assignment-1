package com.codesoom.assignment.controllers;

import com.codesoom.assignment.Fixture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
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
    public static final String USER_NAME = "박범진";
    public static final String PRODUCT_NAME = "상품명";
    private static final Map<String, String> USER_DATA = Map.of(
            "email", Fixture.EMAIL,
            "password", Fixture.PASSWORD,
            "name", USER_NAME
    );
    public static final String SESSION_PATH = "/session";

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

    private Map<String, String> postRequest(Map<String, String> data, String path) throws Exception {
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
        @DisplayName("상품 정보와 유효한 토큰이 주어지면")
        class Context_userData {
            private String accessToken;
            private String userId;

            @BeforeEach
            void prepare() throws Exception {
                Map<String, String> createdUser = postRequest(USER_DATA, Fixture.USER_PATH);
                userId = createdUser.get("id");

                Map<String, String> loginData = Map.of(
                        "email", Fixture.EMAIL,
                        "password", Fixture.PASSWORD
                );

                accessToken = postRequest(loginData, SESSION_PATH).get("accessToken");
            }

            @Test
            @DisplayName("상품을 생성하고 상품 조회 정보와 201을 응답한다")
            void It_returns_productInfo() throws Exception {
                mockMvc.perform(post(Fixture.PRODUCT_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productData(userId)))
                                .header("Authorization", "Bearer " + accessToken))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name", Is.is(PRODUCT_NAME)))
                        .andExpect(jsonPath("$.quantity").value(QUANTITY))
                        .andExpect(jsonPath("$.price").value(PRICE));
            }
        }
    }
}
