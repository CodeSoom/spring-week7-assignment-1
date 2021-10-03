package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TestUtils;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String email;
    private UserRegistrationData userRegistrationData;
    private UserRegistrationData invalidUserRegistrationData;
    private UserModificationData userModificationData;
    private UserModificationData invalidUserModificationData;

    private UserResultData createUser(String email, String password) throws Exception {
        var userData = UserRegistrationData.builder()
                .email(email)
                .password(password)
                .name("nana")
                .build();

        var actions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)));

        return TestUtils.content(actions, UserResultData.class);
    }

    private SessionResponseData login(String email, String password) throws Exception {
        var sessionRequestData = SessionRequestData.builder()
                .email(email)
                .password(password)
                .build();

        var actions = mockMvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequestData)));

        return TestUtils.content(actions, SessionResponseData.class);
    }

    @BeforeEach
    void setupFixtures() {
        email = System.currentTimeMillis() + "@test.com";

        userRegistrationData = UserRegistrationData.builder()
                .name("nana")
                .email(email)
                .password("password")
                .build();

        invalidUserRegistrationData = UserRegistrationData.builder()
                .name("")
                .password("password")
                .build();

        userModificationData = UserModificationData.builder()
                .name("monika")
                .password("password")
                .build();

        invalidUserModificationData = UserModificationData.builder()
                .name("")
                .password("password")
                .build();
    }

    @Nested
    @DisplayName("회원 생성 요청")
    class PostRequest {
        @Test
        @DisplayName("생성된 회원정보와 201 Created HTTP 상태코드로 응답한다.")
        void responsesWithCreatedUserAndCreatedStatusCode() throws Exception {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRegistrationData))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("nana")))
                    .andExpect(jsonPath("$.email", is(email)));
        }

        @Nested
        @DisplayName("유효하지 않은 이름으로 요청하면")
        class WhenNameIsInvalid {
            @ParameterizedTest(name = "400 Bad Request 에러로 응답한다.")
            @ValueSource(strings = {" ", ""})
            void responsesWith400Error(String name) throws Exception {
                invalidUserRegistrationData = UserRegistrationData.builder()
                        .name(name)
                        .email("valid@email.com")
                        .password("valid-password")
                        .build();

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserRegistrationData))
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 이메일로 요청하면")
        class WhenEmailIsInvalid {
            @ParameterizedTest(name = "400 Bad Request 에러로 응답한다.")
            @ValueSource(strings = {" ", "", "222", "email", "@"})
            void responsesWith400Error(String email) throws Exception {
                invalidUserRegistrationData = UserRegistrationData.builder()
                        .name("valid-name")
                        .email(email)
                        .password("valid-password")
                        .build();

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserRegistrationData))
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 비밀번호로 요청하면")
        class WhenPasswordIsInvalid {
            @ParameterizedTest(name = "400 Bad Request 에러로 응답한다.")
            @ValueSource(strings = {" ", "", "2", "22", "222"})
            void responsesWith400Error(String password) throws Exception {
                invalidUserRegistrationData = UserRegistrationData.builder()
                        .name("valid-name")
                        .email("valid@email.com")
                        .password(password)
                        .build();

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserRegistrationData))
                        )
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("회원 삭제 요청")
    class DeleteRequest {
        private UserResultData user;
        private SessionResponseData session;

        @BeforeEach
        void setupForDeleteRequest() throws Exception {
            user = createUser(email, "password");
            session = login(user.getEmail(), "password");
        }

        @Test
        @DisplayName("204 NoContent HTTP 상태코드로 응답한다.")
        void responsesWithNoContentStatusCode() throws Exception {
            mockMvc.perform(delete("/users/" + user.getId())
                            .header(
                                    "Authorization",
                                    String.format("Bearer %s", session.getAccessToken())
                            )
                    )
                    .andExpect(status().isNoContent());
        }

        @Nested
        @DisplayName("토큰이 없으면")
        class WithNoToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(delete("/users/" + user.getId()))
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰이면")
        class WithInvalidToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(delete("/users/" + user.getId())
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken() + "123")
                                )                          )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 회원번호로 요청하면")
        class WithNonExistentId {
            @Test
            @DisplayName("404 NotFound 에러로 응답한다.")
            void responsesWithNotFoundError() throws Exception {
                mockMvc.perform(delete("/users/" + Long.MAX_VALUE)
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken())
                                )
                        )
                        .andExpect(status().isNotFound());
            }
        }

    }

    @Nested
    @DisplayName("회원 수정 요청")
    class PutRequest {
        private UserResultData user;
        private SessionResponseData session;

        @BeforeEach
        void setupForPutRequest() throws Exception {
            user = createUser(email, "password");
            session = login(user.getEmail(), "password");
        }

        @Test
        @DisplayName("수정된 회원정보와 200 Ok HTTP 상태코드로 응답한다.")
        void responsesWithOkStatusCode() throws Exception {
            mockMvc.perform(patch("/users/" + user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userModificationData))
                            .header(
                                    "Authorization",
                                    String.format("Bearer %s", session.getAccessToken())
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("monika")));
        }


        @Nested
        @DisplayName("유효하지 않은 이름으로 요청하면")
        class WhenNameIsInvalid {
            @ParameterizedTest(name = "400 Bad Request 에러로 응답한다.")
            @ValueSource(strings = {" ", ""})
            void responsesWith400Error(String name) throws Exception {
                invalidUserModificationData = UserModificationData.builder()
                        .name(name)
                        .password("valid-password")
                        .build();

                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserModificationData))
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken())
                                )
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 비밀번호로 요청하면")
        class WhenPasswordIsInvalid {
            @ParameterizedTest(name = "400 Bad Request 에러로 응답한다.")
            @ValueSource(strings = {" ", "", "2", "22", "222"})
            void responsesWith400Error(String password) throws Exception {
                invalidUserModificationData = UserModificationData.builder()
                        .name("valid name")
                        .password(password)
                        .build();

                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserModificationData))
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken())
                                )
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("토큰이 없으면")
        class WithNoToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userModificationData))
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰이면")
        class WithInvalidToken {
            @Test
            @DisplayName("401 Unauthorized 에러로 응답한다.")
            void responsesWithUnauthorizedError() throws Exception {
                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userModificationData))
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken() + "123")
                                )                          )
                        .andExpect(status().isUnauthorized());
            }
        }

        // TODO: 해당 케이스가 통과하도록 구현할 것
        @Disabled
        @Nested
        @DisplayName("요청한 사용자가 다른 사용자 정보를 변경하려고 하면")
        class WithForbiddenRequest {
            @Test
            @DisplayName("403 Forbidden 에러로 응답한다.")
            void responsesWithNotFoundError() throws Exception {
                mockMvc.perform(patch("/users/" + (user.getId() - 1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userModificationData))
                                .header(
                                        "Authorization",
                                        String.format("Bearer %s", session.getAccessToken())
                                )                         )
                        .andExpect(status().isForbidden());
            }
        }
    }
}
