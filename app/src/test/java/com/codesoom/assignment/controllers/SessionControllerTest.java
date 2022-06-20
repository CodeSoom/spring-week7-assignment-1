package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.errors.LoginFailException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SessionController에 대한 테스트 클래스
 */
@WebMvcTest(SessionController.class)
class SessionControllerTest {
    private static final String RIGHT_EMAIL = "test@example.com";
    private static final String RIGHT_PASSWORD = "TEST";
    private static final String WRONG_EMAIL = "bad@example.com";
    private static final String WRONG_PASSWORD = "BAD";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private SessionRequestData sessionRequestData;

    // TODO: 로그인 테스트
    //  - 맞는 이메일과 비밀번호로 로그인 시 201코드를 응답하고 유효한 토큰을 반환해야 한다.
    //  - 틀린 이메일로 로그인 시 400 코드를 응답해야 한다.
    //  - 틀린 비밀번호로 로그인 시 400 코드를 응답해야 한다.
    @Nested
    @DisplayName("POST /session 요청 시")
    class Describe_POST_session_request {
        @Nested
        @DisplayName("이메일과 비밀번호가 맞으면")
        class Context_if_email_and_password_right {
            @BeforeEach
            void setUp() {
                sessionRequestData = SessionRequestData.builder()
                        .email(RIGHT_EMAIL)
                        .password(RIGHT_PASSWORD)
                        .build();

                given(authenticationService.login(RIGHT_EMAIL, RIGHT_PASSWORD))
                        .willReturn(VALID_TOKEN);
            }

            @Nested
            @DisplayName("201 코드와 함께 유효한 토큰을 응답한다")
            class It_response_statusCode_201_and_returns_valid_token {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(sessionRequestData))
                    );
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isCreated())
                            .andExpect(content().string(containsString(VALID_TOKEN)));
                }
            }
        }

        @Nested
        @DisplayName("이메일이 틀리면")
        class Context_if_email_wrong {
            @BeforeEach
            void setUp() {
                sessionRequestData = SessionRequestData.builder()
                        .email(WRONG_EMAIL)
                        .password(RIGHT_PASSWORD)
                        .build();

                given(authenticationService.login(WRONG_EMAIL, RIGHT_PASSWORD))
                        .willThrow(new LoginFailException(WRONG_EMAIL));
            }

            @Nested
            @DisplayName("400 코드를 응답한다")
            class It_response_statusCode_400 {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(sessionRequestData))
                    );
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isBadRequest());
                }
            }
        }

        @Nested
        @DisplayName("비밀번호가 틀리면")
        class Context_if_password_wrong {
            @BeforeEach
            void setUp() {
                sessionRequestData = SessionRequestData.builder()
                        .email(RIGHT_EMAIL)
                        .password(WRONG_PASSWORD)
                        .build();

                given(authenticationService.login(RIGHT_EMAIL, WRONG_PASSWORD))
                        .willThrow(new LoginFailException(RIGHT_EMAIL));
            }

            @Nested
            @DisplayName("400 코드를 응답한다")
            class It_response_statusCode_400 {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(sessionRequestData))
                    );
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isBadRequest());
                }
            }
        }
    }

    private String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(object);
    }
}
