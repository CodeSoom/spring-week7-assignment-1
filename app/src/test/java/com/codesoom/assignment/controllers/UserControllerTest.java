package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserNotMatchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String USER_NAME = "name";
    private static final String USER_EMAIL = "email@example.com";
    private static final String USER_PASSWORD = "password";
    private static final String UPDATE_PREFIX = "update";
    private final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("POST /users 는")
    class Describe_create {

        @Nested
        @DisplayName("사용자 등록 요청이 들어오면")
        class Context_with_valid_userRegistrationData {

            UserRegistrationData userRegistrationData;

            @BeforeEach
            void prepare() {
                userRegistrationData = UserRegistrationData.builder()
                        .name(USER_NAME)
                        .email(USER_EMAIL)
                        .password(USER_PASSWORD)
                        .build();

                given(userService.registerUser(any(UserRegistrationData.class)))
                        .will(invocation -> {
                            UserRegistrationData userRegistrationData = invocation.getArgument(0);
                            return User.builder()
                                    .name(userRegistrationData.getName())
                                    .email(userRegistrationData.getEmail())
                                    .build();
                        });
            }

            @Test
            @DisplayName("HttpStatus 201 Created를 응답한다")
            void it_returns_created() throws Exception {
                String content = objectMapper.writeValueAsString(userRegistrationData);
                mockMvc.perform(post("/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(content))
                       .andExpect(status().isCreated())
                       .andExpect(content().string(containsString(USER_NAME)));

                verify(userService).registerUser(any(UserRegistrationData.class));
            }
        }

        @Nested
        @DisplayName("만약 사용자 등록 정보가 없는 요청이 들어오면")
        class Context_with_invalid_userRegistrationData {

            @Test
            @DisplayName("HttpStatus 400 Bad Request를 응답한다")
            void it_returns_bad_request() throws Exception {
                mockMvc.perform(post("/users"))
                       .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("만약 엑세스 토큰이 유효하면")
    class Context_with_valid_access_token {

        private final String VALID_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.neCsyNLzy3lQ4o2yliotWT06FwSGZagaHpKdAkjnGGw";
        private final Long REQUEST_USER_ID = 1L;

        UserModificationData userModificationData;

        @BeforeEach
        void prepare() {
            userModificationData = UserModificationData.builder()
                    .name(UPDATE_PREFIX + USER_NAME)
                    .password(UPDATE_PREFIX + USER_PASSWORD)
                    .build();

            given(authenticationService.parseToken(VALID_ACCESS_TOKEN)).willReturn(REQUEST_USER_ID);
        }

        @Nested
        @DisplayName("접근하는 정보의 주인이 요청자와 같으면")
        class Context_with_same_request_user_and_owner_of_resource {

            @Nested
            @DisplayName("PATCH /users/{id} 는")
            class Describe_update {

                @Nested
                @DisplayName("수정요청이 들어오면")
                class Context_with_request_for_update {

                    @BeforeEach
                    void prepare() {
                        given(userService.updateUser(eq(REQUEST_USER_ID), eq(USER_ID), any(UserModificationData.class)))
                                .will(invocation -> {
                                    Long userId = invocation.getArgument(1);
                                    UserModificationData userModificationData = invocation.getArgument(2);
                                    return User.builder()
                                            .id(userId)
                                            .name(userModificationData.getName())
                                            .build();
                                });
                    }

                    @Test
                    @DisplayName("HttpStatus 200 OK를 응답한다")
                    void it_returns_ok() throws Exception {
                        String content = objectMapper.writeValueAsString(userModificationData);
                        mockMvc.perform(patch("/users/" + USER_ID)
                                .header(AUTHORIZATION, BEARER + VALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString(UPDATE_PREFIX + USER_NAME)));

                        verify(authenticationService, atLeastOnce()).parseToken(VALID_ACCESS_TOKEN);
                        verify(userService).updateUser(eq(USER_ID), eq(USER_ID), any(UserModificationData.class));
                    }
                }
            }

            @Nested
            @DisplayName("DELETE /users/{id} 는")
            class Describe_destroy {

                @Nested
                @DisplayName("삭제요청이 들어오면")
                class Context_with_request_for_delete {

                    @Test
                    @DisplayName("HttpStatus 204 No Content를 응답한다")
                    void it_returns_no_content() throws Exception{
                        mockMvc.perform(delete("/users/" + USER_ID)
                                       .header(AUTHORIZATION, BEARER + VALID_ACCESS_TOKEN))
                               .andExpect(status().isNoContent());
                    }
                }
            }
        }

        @Nested
        @DisplayName("접근하는 정보의 주인이 요청자와 다르면")
        class Context_with_not_same_request_user_and_owner_of_resource {

            private final Long OTHER_USER_ID = 2L;

            @Nested
            @DisplayName("PATCH /users/{id} 는")
            class Describe_update {

                @Nested
                @DisplayName("수정요청이 들어오면")
                class Context_with_request_for_update {

                    @BeforeEach
                    void prepare() {
                        given(userService.updateUser(eq(REQUEST_USER_ID), eq(OTHER_USER_ID), any(UserModificationData.class)))
                                .willThrow(UserNotMatchException.class);
                    }

                    @Test
                    @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                    void it_returns_bad_request() throws Exception {
                        String content = objectMapper.writeValueAsString(userModificationData);
                        mockMvc.perform(patch("/users/" + OTHER_USER_ID)
                                       .header(AUTHORIZATION, BEARER + VALID_ACCESS_TOKEN)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(content))
                                .andExpect(status().isBadRequest());

                        verify(authenticationService, atLeastOnce()).parseToken(VALID_ACCESS_TOKEN);
                        verify(userService).updateUser(eq(REQUEST_USER_ID), eq(OTHER_USER_ID), any(UserModificationData.class));
                    }
                }
            }

            @Nested
            @DisplayName("DELETE /users/{id} 는")
            class Describe_destroy {

                @Nested
                @DisplayName("삭제요청이 들어오면")
                class Context_with_request_for_delete {

                    @BeforeEach
                    void prepare() {
                        given(userService.deleteUser(REQUEST_USER_ID, OTHER_USER_ID)).willThrow(UserNotMatchException.class);
                    }

                    @Test
                    @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                    void it_returns_bad_request() throws Exception {
                        mockMvc.perform(delete("/users/" + OTHER_USER_ID)
                                       .header(AUTHORIZATION, BEARER + VALID_ACCESS_TOKEN))
                               .andExpect(status().isBadRequest());

                        verify(authenticationService, atLeastOnce()).parseToken(VALID_ACCESS_TOKEN);
                        verify(userService).deleteUser(REQUEST_USER_ID, OTHER_USER_ID);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("만약 엑세스 토큰이 유효하지 않다면")
    class Context_with_invalid_access_token {

        private final String INVALID_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.neCsyNLzy3lQ4o2yliotWT06FwSGZagaHpKdAkjnGGG";

        @BeforeEach
        void prepare() {
            given(authenticationService.parseToken(INVALID_ACCESS_TOKEN))
                    .willThrow(InvalidTokenException.class);
        }

        @Nested
        @DisplayName("PATCH /users/{id} 는")
        class Describe_update {

            @Nested
            @DisplayName("수정 요청이 들어오면")
            class Context_with_request_for_update {

                @Test
                @DisplayName("HttpStatus 401 Unauthorized를 응답한다")
                void it_returns_unauthorized() throws Exception {
                    mockMvc.perform(patch("/users/" + USER_ID)
                                   .header(AUTHORIZATION, BEARER + INVALID_ACCESS_TOKEN))
                           .andExpect(status().isUnauthorized());
                }
            }
        }

        @Nested
        @DisplayName("DELETE /users/{id} 는")
        class Describe_destroy {

            @Nested
            @DisplayName("삭제 요청이 들어오면")
            class Context_with_request_for_delete {

                @Test
                @DisplayName("HttpStatus 401 Unauthorized를 응답한다")
                void it_returns_unauthorized() throws Exception {
                    mockMvc.perform(delete("/users/" + USER_ID)
                                   .header(AUTHORIZATION, BEARER + INVALID_ACCESS_TOKEN))
                            .andExpect(status().isUnauthorized());
                }
            }
        }
    }
}
