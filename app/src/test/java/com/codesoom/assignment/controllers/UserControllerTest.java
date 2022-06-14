package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private static final String ACCESS_DENIED_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    private static final Long ACCESS_DENIED_USER_ID = 123L;

    @AfterEach
    void tearDown() {
        reset(userService);
        reset(authenticationService);
    }

    @Nested
    @DisplayName("POST /users 요청은")
    class Describe_create {

        @Nested
        @DisplayName("유효한 요청 데이터가 주어지면")
        class Context_with_valid_request_data {

            @BeforeEach
            void setUp() {
                given(userService.registerUser(any(UserRegistrationData.class)))
                        .will(invocation -> {
                            UserRegistrationData registrationData = invocation.getArgument(0);
                            return User.builder()
                                    .id(13L)
                                    .email(registrationData.getEmail())
                                    .name(registrationData.getName())
                                    .build();
                        });
            }

            @Test
            @DisplayName("생성된 유저를 응답한다.")
            void it_responses_created_user() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"email\":\"tester@example.com\"," +
                                                "\"name\":\"Tester\",\"password\":\"test\"}")
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().string(
                                containsString("\"id\":13")
                        ))
                        .andExpect(content().string(
                                containsString("\"email\":\"tester@example.com\"")
                        ))
                        .andExpect(content().string(
                                containsString("\"name\":\"Tester\"")
                        ));

                verify(userService).registerUser(any(UserRegistrationData.class));
            }
        }

        @Nested
        @DisplayName("유효하지 않은 요청 데이터가 주어지면")
        class Context_with_invalid_request_data {

            @Test
            void it_responses_404_status() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")
                        )
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("PATCH /users/{id} 요청은")
    class Describe_update {

        @Nested
        @DisplayName("유효한 요청 데이터와 인증 토큰이 주어지면")
        class Context_with_valid_data_and_access_token {

            @BeforeEach
            void setUp() {
                given(userService.updateUser(eq(1L), any(UserModificationData.class)))
                        .will(invocation -> {
                            Long id = invocation.getArgument(0);
                            UserModificationData modificationData =
                                    invocation.getArgument(1);
                            return User.builder()
                                    .id(id)
                                    .email("tester@example.com")
                                    .name(modificationData.getName())
                                    .build();
                        });

                given(authenticationService.parseToken(VALID_TOKEN))
                        .willReturn(1L);
            }

            @Test
            @DisplayName("변경된 유저를 응답한다.")
            void it_responses_updated_user() throws Exception {
                mockMvc.perform(
                                patch("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().string(
                                containsString("\"id\":1")
                        ))
                        .andExpect(content().string(
                                containsString("\"name\":\"TEST\"")
                        ));

                verify(userService).updateUser(eq(1L), any(UserModificationData.class));
                verify(authenticationService).parseToken(VALID_TOKEN);
            }
        }

        @Nested
        @DisplayName("인증 토큰이 주어지지 않으면")
        class Context_without_access_token {

            @Test
            @DisplayName("401 status를 응답한다.")
            void it_responses_401_status() throws Exception {
                mockMvc.perform(
                                patch("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("접근 권한이 없는 인증 토큰이 주어지면")
        class Context_access_denied_token {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(ACCESS_DENIED_TOKEN))
                        .willReturn(ACCESS_DENIED_USER_ID);
            }

            @Test
            @DisplayName("403 status를 응답한다.")
            void it_responses_403_status() throws Exception {
                mockMvc.perform(
                                patch("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_DENIED_TOKEN)
                        )
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저 아이디가 주어지면")
        class Context_with_not_existed_user_id {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(VALID_TOKEN))
                        .willReturn(100L);

                given(userService.updateUser(eq(100L), any(UserModificationData.class)))
                        .willThrow(new UserNotFoundException(100L));
            }

            @Test
            @DisplayName("404 status를 응답한다.")
            void it_responses_404_status() throws Exception {
                mockMvc.perform(
                                patch("/users/100")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isNotFound());

                verify(userService)
                        .updateUser(eq(100L), any(UserModificationData.class));
            }
        }
    }

    @Nested
    @DisplayName("DELETE /users 요청은")
    class Describe_destroy {

        @Nested
        @DisplayName("권한이 있는 토큰이 주어지면")
        class Context_with_access_token {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(VALID_TOKEN))
                        .willReturn(1L);
            }

            @Test
            @DisplayName("204 status를 응답한다.")
            void it_responses_204_status() throws Exception {
                mockMvc.perform(
                            delete("/users/1")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isNoContent());

                verify(userService).deleteUser(1L);
            }
        }

        @Nested
        @DisplayName("권한이 없는 토큰이 주어지면")
        class Context_with_access_denied_token {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(ACCESS_DENIED_TOKEN))
                        .willReturn(ACCESS_DENIED_USER_ID);
            }

            @Test
            @DisplayName("403 status를 응답한다.")
            void it_responses_403_status() throws Exception {
                mockMvc.perform(
                                delete("/users/1")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_DENIED_TOKEN)
                        )
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_access_denied_access_token {

            @Test
            @DisplayName("401 status를 응답한다.")
            void destroyWithExistedId() throws Exception {
                mockMvc.perform(
                                delete("/users/1")
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저 아이디가 주어지면")
        class Context_with_not_existed_id {

            @BeforeEach
            void setUp() {
                given(authenticationService.parseToken(VALID_TOKEN))
                        .willReturn(100L);

                given(userService.deleteUser(100L))
                        .willThrow(new UserNotFoundException(100L));
            }

            @Test
            void destroyWithNotExistedId() throws Exception {
                mockMvc.perform(
                            delete("/users/100")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
                        )
                        .andExpect(status().isNotFound());

                verify(userService).deleteUser(100L);
            }
        }
    }
}
