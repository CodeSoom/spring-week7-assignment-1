package com.codesoom.assignment.user.presentation;

import com.codesoom.assignment.MockMvcCharacterEncodingCustomizer;
import com.codesoom.assignment.common.authorization.UserAuthorizationAop;
import com.codesoom.assignment.common.utils.JsonUtil;
import com.codesoom.assignment.session.application.AuthenticationService;
import com.codesoom.assignment.session.application.exception.InvalidTokenException;
import com.codesoom.assignment.support.UserFixture;
import com.codesoom.assignment.user.application.UserService;
import com.codesoom.assignment.user.application.exception.UserNotFoundException;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.presentation.dto.UserModificationData;
import com.codesoom.assignment.user.presentation.dto.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.codesoom.assignment.support.AuthHeaderFixture.INVALID_VALUE_TOKEN_1;
import static com.codesoom.assignment.support.AuthHeaderFixture.VALID_TOKEN_1;
import static com.codesoom.assignment.support.AuthHeaderFixture.VALID_TOKEN_2;
import static com.codesoom.assignment.support.IdFixture.ID_2;
import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.IdFixture.ID_MIN;
import static com.codesoom.assignment.support.UserFixture.USER_1;
import static com.codesoom.assignment.support.UserFixture.USER_2;
import static com.codesoom.assignment.support.UserFixture.USER_INVALID_EMAIL;
import static com.codesoom.assignment.support.UserFixture.USER_INVALID_NAME;
import static com.codesoom.assignment.support.UserFixture.USER_INVALID_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import({MockMvcCharacterEncodingCustomizer.class, AopAutoConfiguration.class, UserAuthorizationAop.class})
@DisplayName("UserController 웹 유닛 테스트")
class UserControllerMockTest {

    private static final String REQUEST_USER_URL = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUpClearMock() {
        Mockito.clearInvocations(userService);
    }

    @BeforeEach
    void setUpParseToken() {
        given(authenticationService.parseToken(eq(VALID_TOKEN_1.토큰_값())))
                .willReturn(VALID_TOKEN_1.아이디());

        given(authenticationService.parseToken(eq(VALID_TOKEN_2.토큰_값())))
                .willReturn(VALID_TOKEN_2.아이디());

        given(authenticationService.parseToken(eq(INVALID_VALUE_TOKEN_1.토큰_값())))
                .willThrow(new InvalidTokenException(INVALID_VALUE_TOKEN_1.토큰_값()));
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 회원_등록_API는 {

        @Nested
        @DisplayName("유효한 회원 정보가 주어지면")
        class Context_with_valid_user {

            @BeforeEach
            void setUp() {
                given(userService.registerUser(any(UserRegistrationData.class)))
                        .will(invocation -> {
                            UserRegistrationData registrationData = invocation.getArgument(0);
                            return User.builder()
                                    .id(ID_MIN.value())
                                    .email(registrationData.getEmail())
                                    .name(registrationData.getName())
                                    .build();
                        });
            }

            @Test
            @DisplayName("201 코드로 응답한다")
            void it_responses_201() throws Exception {
                ResultActions perform = 회원_등록_API_요청(USER_1);

                perform.andExpect(status().isCreated());

                verify(userService).registerUser(any(UserRegistrationData.class));
            }
        }

        @Nested
        @DisplayName("유효하지 않은 회원 정보가 주어지면")
        class Context_with_invalid_user {

            @Nested
            @DisplayName("이메일이 공백일 경우")
            class Context_with_empty_email {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 회원_등록_API_요청(USER_INVALID_EMAIL);

                    perform.andExpect(status().isBadRequest());

                    verify(userService, never()).registerUser(any(UserRegistrationData.class));
                }
            }

            @Nested
            @DisplayName("이름이 공백일 경우")
            class Context_with_empty_name {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 회원_등록_API_요청(USER_INVALID_NAME);

                    perform.andExpect(status().isBadRequest());

                    verify(userService, never()).registerUser(any(UserRegistrationData.class));
                }
            }

            @Nested
            @DisplayName("비밀번호가 4글자 미만일 경우")
            class Context_with_invalid_password {

                @Test
                @DisplayName("400 코드로 응답한다")
                void it_responses_400() throws Exception {
                    ResultActions perform = 회원_등록_API_요청(USER_INVALID_PASSWORD);

                    perform.andExpect(status().isBadRequest());

                    verify(userService, never()).registerUser(any(UserRegistrationData.class));
                }
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 회원_수정_API는 {

        @Nested
        @DisplayName("인증 토큰이 없다면")
        class Context_with_token_not_exist {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = mockMvc.perform(
                        patch(REQUEST_USER_URL + "/" + ID_MIN.value())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.writeValueAsString(USER_1.수정_요청_데이터_생성()))
                );

                perform.andExpect(status().isUnauthorized());

                verify(userService, never()).updateUser(eq(ID_MIN.value()), any(UserModificationData.class));
            }
        }

        @Nested
        @DisplayName("유효하지 않은 인증 토큰이 주어지면")
        class Context_with_invalid_token_value {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = 회원_수정_API_요청(
                        INVALID_VALUE_TOKEN_1.인증_헤더값(),
                        ID_MIN.value(),
                        USER_1
                );

                perform.andExpect(status().isUnauthorized());

                verify(userService, never()).updateUser(eq(ID_MIN.value()), any(UserModificationData.class));
            }
        }

        @Nested
        @DisplayName("다른 사람의 인증 토큰이 주어지면")
        class Context_with_different_id_token_and_request {

            @Test
            @DisplayName("403 코드로 응답한다")
            void it_returns_403() throws Exception {
                ResultActions perform = 회원_수정_API_요청(
                        VALID_TOKEN_2.인증_헤더값(),
                        ID_MIN.value(),
                        USER_1
                );

                perform.andExpect(status().isForbidden());

                verify(userService, never()).updateUser(any(Long.class), any(UserModificationData.class));
            }
        }

        @Nested
        @DisplayName("유효한 인증 토큰이 주어지고")
        class Context_with_valid_token_value {

            @Nested
            @DisplayName("찾을 수 없는 id가 주어질 때")
            class Context_with_not_exist_id {

                @BeforeEach
                void setUp() {
                    given(userService.updateUser(eq(ID_2.value()), any(UserModificationData.class)))
                            .willThrow(new UserNotFoundException(ID_2.value()));
                }

                @Test
                @DisplayName("404 코드로 응답한다")
                void it_responses_404() throws Exception {
                    ResultActions perform = 회원_수정_API_요청(
                            VALID_TOKEN_2.인증_헤더값(),
                            ID_2.value(),
                            USER_2
                    );

                    perform.andExpect(status().isNotFound());

                    verify(userService).updateUser(eq(ID_2.value()), any(UserModificationData.class));
                }
            }

            @Nested
            @DisplayName("찾을 수 있는 id가 주어질 때")
            class Context_with_exist_id {

                @BeforeEach
                void setUp() {
                    given(userService.updateUser(eq(ID_MIN.value()), any(UserModificationData.class)))
                            .willReturn(USER_1.회원_엔티티_생성(ID_MIN.value()));
                }

                @Nested
                @DisplayName("유효하지 않은 회원 정보가 주어진다면")
                class Context_with_invalid_user {

                    @Nested
                    @DisplayName("이름이 공백일 경우")
                    class Context_with_empty_name {

                        @Test
                        @DisplayName("400 코드로 응답한다")
                        void it_responses_400() throws Exception {
                            ResultActions perform = 회원_수정_API_요청(
                                    VALID_TOKEN_1.인증_헤더값(),
                                    ID_MIN.value(),
                                    USER_INVALID_NAME
                            );

                            perform.andExpect(status().isBadRequest());

                            verify(userService, never()).updateUser(eq(ID_MIN.value()), any(UserModificationData.class));
                        }
                    }

                    @Nested
                    @DisplayName("비밀번호가 4글자 미만일 경우")
                    class Context_with_invalid_password {

                        @Test
                        @DisplayName("400 코드로 응답한다")
                        void it_responses_400() throws Exception {
                            ResultActions perform = 회원_수정_API_요청(
                                    VALID_TOKEN_1.인증_헤더값(),
                                    ID_MIN.value(),
                                    USER_INVALID_PASSWORD
                            );

                            perform.andExpect(status().isBadRequest());

                            verify(userService, never()).updateUser(eq(ID_MIN.value()), any(UserModificationData.class));
                        }
                    }
                }

                @Nested
                @DisplayName("유효한 회원 정보가 주어진다면")
                class Context_with_valid_user {

                    @Test
                    @DisplayName("200 코드로 응답한다")
                    void it_responses_200() throws Exception {
                        ResultActions perform = 회원_수정_API_요청(
                                VALID_TOKEN_1.인증_헤더값(),
                                ID_MIN.value(),
                                USER_1
                        );

                        perform.andExpect(status().isOk());

                        verify(userService).updateUser(eq(ID_MIN.value()), any(UserModificationData.class));
                    }
                }
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 회원_삭제_API는 {

        @Nested
        @DisplayName("인증 토큰이 없다면")
        class Context_with_not_exist_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = mockMvc.perform(
                        delete(REQUEST_USER_URL + "/" + ID_MIN.value())
                );

                perform.andExpect(status().isUnauthorized());

                verify(userService, never()).deleteUser(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 인증 토큰이 주어지면")
        class Context_with_invalid_token {

            @Test
            @DisplayName("401 코드로 응답한다")
            void it_responses_401() throws Exception {
                ResultActions perform = 회원_삭제_API_요청(
                        INVALID_VALUE_TOKEN_1.인증_헤더값(),
                        ID_MIN.value()
                );

                perform.andExpect(status().isUnauthorized());

                verify(userService, never()).deleteUser(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("유효한 인증 토큰이 주어지고")
        class Context_with_valid_token {

            @Nested
            @DisplayName("찾을 수 있는 id가 주어지면")
            class Context_with_exist_id {

                @Test
                @DisplayName("204 코드로 응답한다")
                void it_responses_204() throws Exception {
                    ResultActions perform = 회원_삭제_API_요청(
                            VALID_TOKEN_1.인증_헤더값(),
                            ID_MIN.value()
                    );

                    perform.andExpect(status().isNoContent());

                    verify(userService).deleteUser(ID_MIN.value());
                }
            }

            @Nested
            @DisplayName("찾을 수 없는 id가 주어지면")
            class Context_with_not_exist_id {

                @BeforeEach
                void setUp() {
                    given(userService.deleteUser(ID_MAX.value()))
                            .willThrow(new UserNotFoundException(ID_MAX.value()));
                }

                @Test
                @DisplayName("404 코드로 응답한다")
                void it_responses_404() throws Exception {
                    ResultActions perform = 회원_삭제_API_요청(
                            VALID_TOKEN_1.인증_헤더값(),
                            ID_MAX.value()
                    );

                    perform.andExpect(status().isNotFound());

                    verify(userService).deleteUser(ID_MAX.value());
                }
            }
        }
    }


    private ResultActions 회원_등록_API_요청(UserFixture userFixture) throws Exception {
        return mockMvc.perform(
                post(REQUEST_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValueAsString(userFixture.등록_요청_데이터_생성()))
        );
    }

    private ResultActions 회원_수정_API_요청(String authHeader,
                                       Long userId,
                                       UserFixture userFixture) throws Exception {
        return mockMvc.perform(
                patch(REQUEST_USER_URL + "/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValueAsString(userFixture.수정_요청_데이터_생성()))
        );
    }

    private ResultActions 회원_삭제_API_요청(String authHeader,
                                       Long userId) throws Exception {
        return mockMvc.perform(
                delete(REQUEST_USER_URL + "/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
        );
    }
}
