package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController 클래스")
@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX0FETUlOIn0." +
            "gY_MzvqqZP5DbHVXGKn-ZGIyZAd3PpOdAleeJvp23tg";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX1VTRVIifQ." +
            "SgLtYfTVUdvPF-gIP006U-_B7-wWMSUcJD3eoSOxHsE";
    private static final String INVALID_TOKEN = VALID_TOKEN + "WRONG";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    private final Long existingId = 1L;
    private final Long notExistingId = 100L;

    private UserRegistrationData validUserRegistrationData;
    private UserRegistrationData invalidUserRegistrationData;
    private UserRegistrationData duplicateUserRegistrationData;

    private UserModificationData validUserModificationData;
    private UserModificationData invalidUserModificationData;

    private User user;

    private Claims validClaims;
    private Claims invalidClaims;
    private Claims adminClaims;

    @BeforeEach
    void setUp() {
        validUserRegistrationData = createUserRegistrationData("valid@test.com");
        invalidUserRegistrationData = createUserRegistrationData("");
        duplicateUserRegistrationData = createUserRegistrationData("duplicate@test.com");

        validUserModificationData = createUserModificationData("이름수정");
        invalidUserModificationData = createUserModificationData("");

        user = User.builder()
                .id(existingId)
                .email("email@test.com")
                .name("name")
                .password("12345678")
                .build();

        validClaims = Jwts.claims();
        validClaims.put("userId", user.getId());
        validClaims.put("role", user.getRole().getName());

        invalidClaims = Jwts.claims();
        invalidClaims.put("userId", user.getId() + 1000L);
        invalidClaims.put("role", user.getRole().getName());

        adminClaims = Jwts.claims();
        adminClaims.put("userId", 9999L);
        adminClaims.put("role", "ROLE_ADMIN");

        given(authenticationService.parseToken(VALID_TOKEN))
                .willReturn(validClaims);

        given(authenticationService.parseToken(INVALID_TOKEN))
                .willReturn(invalidClaims);

        given(authenticationService.parseToken(ADMIN_TOKEN))
                .willReturn(adminClaims);
    }

    @Nested
    @DisplayName("POST 요청은")
    class Describe_POST {
        @Nested
        @DisplayName("올바른 회원 가입 정보가 주어진다면")
        class Context_with_a_valid_user_registration_data {
            @BeforeEach
            void setUp() {
                given(userService.registerUser(any(UserRegistrationData.class)))
                        .willReturn(user);
            }

            @Test
            @DisplayName("생성된 회원과 상태코드 201 Created 를 응답한다.")
            void it_responds_the_created_user_and_status_code_201() throws Exception {
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRegistrationData)))
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("name").exists())
                        .andExpect(jsonPath("email").exists())
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 회원 가입 정보가 주어진다면")
        class Context_with_an_invalid_user_registration_data {
            @Test
            @DisplayName("상태코드 400 Bad Request 를 응답한다.")
            void it_responds_status_code_400() throws Exception {
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserRegistrationData)))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("주어진 이메일이 중복되었다면")
        class Context_with_an_duplicate_user_registration_data {
            @BeforeEach
            void setUp() {
                given(userService.registerUser(any(UserRegistrationData.class)))
                        .willThrow(new UserEmailDuplicationException(duplicateUserRegistrationData.getEmail()));
            }

            @Test
            @DisplayName("에러메시지와 상태코드 400 Bad Request 를 응답한다.")
            void it_responds_the_error_message_and_status_code_400() throws Exception {
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUserRegistrationData)))
                        .andExpect(jsonPath("name").doesNotExist())
                        .andExpect(jsonPath("message").exists())
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("PATCH 요청은")
    class Describe_PATCH {
        @Nested
        @DisplayName("올바른 회원 수정 정보가 주어진다면")
        class Context_with_an_valid_user_modification_data {
            @BeforeEach
            void setUp() {
                given(userService.updateUser(eq(existingId), any(UserModificationData.class)))
                        .willReturn(user);
            }

            @Test
            @DisplayName("수정된 회원과 상태코드 200 OK 를 응답한다.")
            void it_responds_the_updated_user_and_status_code_200() throws Exception {
                mockMvc.perform(patch("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("name").exists())
                        .andExpect(jsonPath("email").exists())
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 회원 수정 정보가 주어진다면")
        class Context_with_an_invalid_user_modification_data {
            @Test
            @DisplayName("상태코드 400 Bad Request 를 응답한다.")
            void it_responds_the_error_message_and_status_code_400() throws Exception {
                mockMvc.perform(patch("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .content(objectMapper.writeValueAsString(invalidUserModificationData)))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 회원 id가 주어진다면")
        class Context_with_not_existing_user_id {
            @Test
            @DisplayName("상태코드 403 Forbidden 를 응답한다.")
            void it_responds_status_code_403() throws Exception {
                mockMvc.perform(patch("/users/{id}", notExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("액세스 토큰이 유효 하지 않다면")
        class Context_with_invalid_access_token {
            @Test
            @DisplayName("상태코드 403 Forbidden 를 응답한다.")
            void it_responds_status_code_403() throws Exception {
                mockMvc.perform(patch("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("액세스 토큰이 없다면")
        class Context_without_access_token {
            @Test
            @DisplayName("상태코드 401 Unauthorized 를 응답한다.")
            void it_responds_status_code_401() throws Exception {
                mockMvc.perform(patch("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("관리자가 존재하는 회원을 수정한다면")
        class Context_when_admin_edits_existing_user {
            @BeforeEach
            void setUp() {
                given(userService.updateUser(eq(existingId), any(UserModificationData.class)))
                        .willReturn(user);
            }

            @Test
            @DisplayName("수정된 회원과 상태코드 200 OK 를 응답한다.")
            void it_responds_the_updated_user_and_status_code_200() throws Exception {
                mockMvc.perform(patch("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ADMIN_TOKEN)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("name").exists())
                        .andExpect(jsonPath("email").exists())
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("관리자가 존재하지 않는 회원을 수정한다면")
        class Context_when_admin_edits_not_existing_user {
            @BeforeEach
            void setUp() {
                given(userService.updateUser(eq(notExistingId), any(UserModificationData.class)))
                        .willThrow(new UserNotFoundException(notExistingId));
            }

            @Test
            @DisplayName("에러메시지와 상태코드 404 Not Found 를 응답한다.")
            void it_responds_the_error_message_and_status_code_404() throws Exception {
                mockMvc.perform(patch("/users/{id}", notExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ADMIN_TOKEN)
                        .content(objectMapper.writeValueAsString(validUserModificationData)))
                        .andExpect(jsonPath("name").doesNotExist())
                        .andExpect(jsonPath("message").exists())
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("DELETE 요청은")
    class Describe_DELETE {
        @Nested
        @DisplayName("존재하는 회원 id가 주어진다면")
        class Context_with_an_existing_user_id {
            @Test
            @DisplayName("상태코드 204 No Content 를 응답한다.")
            void it_responds_status_code_204() throws Exception {
                mockMvc.perform(delete("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 회원 id가 주어진다면")
        class Context_with_not_existing_user_id {
            @BeforeEach
            void setUp() {
                given(userService.deleteUser(notExistingId))
                        .willThrow(new UserNotFoundException(notExistingId));
            }

            @Test
            @DisplayName("에러메시지와 상태코드 404 Not Found 를 응답한다.")
            void it_responds_the_error_message_and_status_code_404() throws Exception {
                mockMvc.perform(delete("/users/{id}", notExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("name").doesNotExist())
                        .andExpect(jsonPath("message").exists())
                        .andExpect(status().isNotFound());
            }
        }

    }

    private UserModificationData createUserModificationData(String name) {
        return UserModificationData.builder()
                .name(name)
                .password("12345678")
                .build();
    }

    private UserRegistrationData createUserRegistrationData(String email) {
        return UserRegistrationData.builder()
                .email(email)
                .name("name")
                .password("12345678")
                .build();
    }

}
