package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        Mockito.doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            UserModificationData modificationData =
                    invocation.getArgument(1);
            return User.builder()
                    .id(id)
                    .email("tester@example.com")
                    .name(modificationData.getName())
                    .build();
        })
                .when(userService).updateUser(eq(1L), any(UserModificationData.class));

        Mockito.doThrow(new UserNotFoundException(100L))
                .when(userService).updateUser(eq(100L), any(UserModificationData.class));

        Mockito.doThrow(new UserNotFoundException(100L))
                .when(userService).deleteUser(100L);

        Mockito.doReturn(1L)
                .when(authenticationService).parseToken(VALID_TOKEN);

        Mockito.doThrow(new InvalidTokenException(INVALID_TOKEN))
                .when(authenticationService).parseToken(INVALID_TOKEN);
    }

    @Test
    void updateUserWithValidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"id\":1")
                ))
                .andExpect(content().string(
                        containsString("\"name\":\"TEST\"")
                ));

        verify(userService).updateUser(eq(1L), any(UserModificationData.class));
    }

    @Test
    void updateUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithNotExsitedId() throws Exception {
        mockMvc.perform(
                patch("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isNotFound());

        verify(userService)
                .updateUser(eq(100L), any(UserModificationData.class));
    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/1")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/100")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }

    @Nested
    @DisplayName("[POST] /users 요청은")
    class Describe_post_users {
        @BeforeEach
        void setup() {
            Mockito.doAnswer(invocation -> {
                UserRegistrationData registrationData = invocation.getArgument(0);

                if (
                        registrationData.getEmail().isEmpty()
                                || registrationData.getName().isEmpty()
                ) {
                    throw new MappingException("invalid user data");
                }

                if (registrationData.getEmail().equals("duplicated@email.com")) {
                    throw new UserEmailDuplicationException("duplicated@email.com");
                }

                return User.builder()
                        .id(13L)
                        .email(registrationData.getEmail())
                        .name(registrationData.getName())
                        .build();
            })
                    .when(userService).registerUser(any(UserRegistrationData.class));
        }

        @Nested
        @DisplayName("주어진 정보가 올바를 때")
        class Context_with_valid_attributes {
            @Test
            @DisplayName("created 와 생성된 user 를 응답한다.")
            void It_respond_created_and_created_user() throws Exception {
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
            }
        }

        @Nested
        @DisplayName("주어진 정보가 올바르지 않을 때")
        class Context_with_invalid_attributes {
            @Test
            @DisplayName("bad request 를 응답한다.")
            void It_respond_bad_request() throws Exception {
                mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("주어진 email 을 등록된 유저가 이미 보유중일 때")
        class Context_with_already_has_given_email_who_registered_user {
            @Test
            @DisplayName("bad request 를 응답한다.")
            void It_respond_bad_request() throws Exception {
                mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"duplicated@email.com\"," +
                                        "\"name\":\"Tester\",\"password\":\"test\"}")
                )
                        .andExpect(status().isBadRequest());
            }
        }
    }
}
