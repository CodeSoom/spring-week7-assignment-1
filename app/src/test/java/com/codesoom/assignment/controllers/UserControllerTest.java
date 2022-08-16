package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
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
@DisplayName("UserController 클래스의")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    public static final String USER = "USER";
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String NAME = "박범진";
    private static final Map<String, String> USER_DATA = Map.of(
            "email", EMAIL,
            "password", PASSWORD,
            "name", NAME
    );

    private Map<String, String> createUser(Map<String, String> userData) throws Exception {
        return objectMapper.readValue(mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<>(){});
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("register 메서드는")
    class Describe_register {
        @Nested
        @DisplayName("유저 정보가 주어지면")
        class Context_userData {
            @Test
            @DisplayName("유저와 201을 응답한다")
            void It_returns_user() throws Exception {
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(USER_DATA)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.email", Is.is(EMAIL)))
                        .andExpect(jsonPath("$.name", Is.is(NAME)))
                        .andExpect(jsonPath("$.role", Is.is(USER)));
            }
        }

        @Nested
        @DisplayName("중복되는 이메일이 주어지면")
        class Context_with_email {
            void prepare() throws Exception {
                createUser(USER_DATA);
            }

            @Test
            @DisplayName("예외 메시지와 400을 응답한다")
            void it_returns_exception() throws Exception {
                prepare();

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(USER_DATA)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").isString());
            }
        }
    }
}
