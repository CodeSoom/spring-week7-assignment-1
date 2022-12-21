package com.codesoom.assignment.session.presentation;

import com.codesoom.assignment.common.utils.JsonUtil;
import com.codesoom.assignment.session.application.AuthenticationService;
import com.codesoom.assignment.session.application.exception.LoginFailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.codesoom.assignment.support.AuthHeaderFixture.VALID_TOKEN_1;
import static com.codesoom.assignment.support.UserFixture.USER_1;
import static com.codesoom.assignment.support.UserFixture.USER_1_Wrong_PASSWORD;
import static com.codesoom.assignment.support.UserFixture.USER_2;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    private static final String REQUEST_SESSION_URL = "/session";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 로그인_API는 {

        @Nested
        @DisplayName("유효한 정보가 주어지면")
        class Context_with_valid_data {

            @BeforeEach
            void setUp() {
                given(authenticationService.login(USER_1.이메일(), USER_1.비밀번호()))
                        .willReturn(VALID_TOKEN_1.토큰_값());
            }

            @Test
            @DisplayName("201 코드로 응답한다")
            void it_responses_201() throws Exception {
                ResultActions perform = mockMvc.perform(
                        post(REQUEST_SESSION_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(USER_1.로그인_요청_데이터_생성()))
                );

                perform.andExpect(status().isCreated());
                perform.andExpect(content().string(containsString(VALID_TOKEN_1.토큰_값())));

                verify(authenticationService).login(USER_1.이메일(), USER_1.비밀번호());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 이메일이 주어지면")
        class Context_with_invalid_email {

            @BeforeEach
            void setUp() {
                given(authenticationService.login(USER_2.이메일(), USER_2.비밀번호()))
                        .willThrow(new LoginFailException(USER_2.이메일()));
            }

            @Test
            @DisplayName("400 코드로 응답한다")
            void it_responses_400() throws Exception {
                ResultActions perform = mockMvc.perform(
                        post(REQUEST_SESSION_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(USER_2.로그인_요청_데이터_생성()))
                );

                perform.andExpect(status().isBadRequest());

                verify(authenticationService).login(USER_2.이메일(), USER_2.비밀번호());
            }
        }

        @Nested
        @DisplayName("틀린 비밀번호가 주어지면")
        class Context_with_invalid_password {

            @BeforeEach
            void setUp() {
                given(authenticationService.login(USER_1_Wrong_PASSWORD.이메일(), USER_1_Wrong_PASSWORD.비밀번호()))
                        .willThrow(new LoginFailException(USER_1_Wrong_PASSWORD.이메일()));
            }

            @Test
            @DisplayName("400 코드로 응답한다")
            void it_responses_400() throws Exception {
                ResultActions perform = mockMvc.perform(
                        post(REQUEST_SESSION_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(USER_1_Wrong_PASSWORD.로그인_요청_데이터_생성()))
                );

                perform.andExpect(status().isBadRequest());

                verify(authenticationService).login(USER_1_Wrong_PASSWORD.이메일(), USER_1_Wrong_PASSWORD.비밀번호());
            }
        }
    }
}
