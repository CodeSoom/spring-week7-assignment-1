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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController 클래스의")
public class ProductControllerTest {
    private static final Map<String, String> USER_DATA = Map.of(
            "email", Fixture.EMAIL,
            "password", Fixture.PASSWORD,
            "name", Fixture.USER_NAME
    );

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> productData(String userId) {
        return Map.of(
                "userId", userId,
                "name", Fixture.PRODUCT_NAME,
                "quantity", Fixture.QUANTITY,
                "price", Fixture.PRICE
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

                accessToken = postRequest(loginData, Fixture.SESSION_PATH).get("accessToken");
            }

            @Test
            @DisplayName("상품을 생성하고 상품 조회 정보와 201을 응답한다")
            void It_returns_productInfo() throws Exception {
                mockMvc.perform(post(Fixture.PRODUCT_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productData(userId)))
                                .header("Authorization", "Bearer " + accessToken))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.user.id").value(userId))
                        .andExpect(jsonPath("$.name", Is.is(Fixture.PRODUCT_NAME)))
                        .andExpect(jsonPath("$.quantity").value(Fixture.QUANTITY))
                        .andExpect(jsonPath("$.price").value(Fixture.PRICE));
            }
        }
    }
}
