package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController")
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String UNAUTHORIZED_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    private static final String UNAUTHENTICATED_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD5";
    private final Long USER_ID_DENIED = 444L;
    private final Long USER_ID_NOT_EXISTING = 100L;
    private final Long USER_ID = 1L;
    private final Long USER_ID_CREATING = 123L;

    @Nested
    @DisplayName("create 메소드는")
    class Describe_create {

        @Nested
        @DisplayName("요청 데이터와 토큰이 모두 유효하다면")
        class Context_with_valid_request_data_and_token {

            @BeforeEach
            void setUp() {
                given(userService.registerUser(any(UserRegistrationData.class)))
                        .will(invocation -> {
                            UserRegistrationData registrationData = invocation.getArgument(0);
                            return User.builder()
                                    .id(USER_ID_CREATING)
                                    .email(registrationData.getEmail())
                                    .name(registrationData.getName())
                                    .build();
                        });

                given(authenticationService.parseToken(VALID_TOKEN)).willReturn(USER_ID_CREATING);
            }

            @Test
            @DisplayName("HTTP Status Code 201 CREATED 반환한다")
            void it_responses_201() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"tester@example.com\"," +
                                                "\"name\":\"Tester\",\"password\":\"test\"}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 요청 데이터를 전달 받으면")
        class Context_with_invalid_request_data {
            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(VALID_TOKEN)).willReturn(USER_ID);
            }

            @Test
            @DisplayName("HTTP Status Code 400 BAD REQUEST 반환한다")
            void it_responses_400() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("인증되지 않은 토큰을 전달받으면")
        class Context_access_unauthenticated_token {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(UNAUTHORIZED_TOKEN))
                        .willThrow(new InvalidTokenException(UNAUTHORIZED_TOKEN));
            }

            @Test
            @DisplayName("HTTP Status Code 401 UNAUTHORIZED 반환한다")
            void it_responses_401() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"tester@example.com\"," +
                                                "\"name\":\"Tester\",\"password\":\"test\"}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + UNAUTHORIZED_TOKEN)
                        )
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}
