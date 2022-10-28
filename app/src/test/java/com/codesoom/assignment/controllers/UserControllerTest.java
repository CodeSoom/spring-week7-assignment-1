package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserController 클래스")
class UserControllerTest {

    private static final String NAME = "tester";
    private static final String EMAIL = "tester@example.com";
    private static final String PASSWORD = "tester12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("create 메소드는")
    class Describe_create {

        @Nested
        @DisplayName("입력값이 올바른 경우")
        class Context_with_valid_input {

            @Test
            @DisplayName("201 응답을 생성한다")
            void it_creates_created() throws Exception {
                mockMvc.perform(post("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getCreateUserRequest())))
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("입력값이 올바르지 않은 경우")
        class Context_with_invalid_input {
            private final UserRegistrationData registrationData = UserRegistrationData.builder()
                    .name("")
                    .email("")
                    .password("")
                    .build();

            @Test
            @DisplayName("400 응답을 생성한다")
            void it_creates_bad_request() throws Exception {
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationData)))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Describe_update {
        private final String AUTHORIZATION = "Authorization";
        private final String BEARER = "Bearer ";

        @Nested
        @DisplayName("본인의 회원 정보 수정 요청이 들어오면")
        class Context_with_valid_user_and_input {
            private Long userId;
            private String validToken;

            @BeforeEach
            void setUp() {
                User user = userService.registerUser(getCreateUserRequest());
                userId = user.getId();
                validToken = jwtUtil.encode(userId);
            }

            @Test
            @DisplayName("200 응답을 생성한다")
            void it_creates_ok() throws Exception {
                mockMvc.perform(patch("/users/{id}", userId)
                                .header(AUTHORIZATION, BEARER + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getUpdateUserRequest())))
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("본인이 아닌 다른 회원의 정보 수정 요청이 들어오면")
        class Context_with_other_user {
            private Long otherUserId;
            private String validToken;

            @BeforeEach
            void setUp() {
                User user = userService.registerUser(getCreateUserRequest());
                validToken = jwtUtil.encode(user.getId());
                otherUserId = user.getId() + 100;
            }

            @Test
            @DisplayName("403 응답을 생성한다")
            void it_creates_forbidden() throws Exception {
                mockMvc.perform(patch("/users/{id}", otherUserId)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, BEARER + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getUpdateUserRequest())))
                        .andExpect(status().isForbidden());
            }
        }
    }

    @Nested
    @DisplayName("delete 메소드는")
    class Describe_delete {

        @Nested
        @DisplayName("찾을 수 있는 회원 정보가 주어지면")
        class Context_with_exist_user {
            private Long id;

            @BeforeEach
            void setUp() {
                User user = userService.registerUser(getCreateUserRequest());
                id = user.getId();
            }

            @Test
            @DisplayName("204 응답을 생성한다")
            void it_creates_no_content() throws Exception {
                mockMvc.perform(delete("/users/{id}", id))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 회원 정보가 주어지면")
        class Context_with_not_exist_user {
            private Long id;

            @BeforeEach
            void setUp() {
                User user = userService.registerUser(getCreateUserRequest());
                id = user.getId();
                userService.deleteUser(id);
            }

            @Test
            @DisplayName("404 응답을 생성한다")
            void it_creates_not_found() throws Exception {
                mockMvc.perform(delete("/users/{id}", id))
                        .andExpect(status().isNotFound());
            }
        }
    }

    private UserModificationData getUpdateUserRequest() {
        return UserModificationData.builder()
                .name(NAME)
                .password(PASSWORD)
                .build();
    }

    private UserRegistrationData getCreateUserRequest() {
        return UserRegistrationData.builder()
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
}
