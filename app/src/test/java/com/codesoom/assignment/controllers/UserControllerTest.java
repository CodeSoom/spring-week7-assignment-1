package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

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

        given(userService.updateUser(eq(100L), any(UserModificationData.class)))
                .willThrow(new UserNotFoundException(100L));

        given(userService.deleteUser(100L))
                .willThrow(new UserNotFoundException(100L));
    }

    @Test
    void registerUserWithValidAttributes() throws Exception {
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

    @Test
    void registerUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        )
                .andExpect(status().isBadRequest());
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
    }

    @Test
    void updateUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"\"}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithNotExsitedId() throws Exception {
        mockMvc.perform(
                patch("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
        )
                .andExpect(status().isNotFound());

        verify(userService)
                .updateUser(eq(100L), any(UserModificationData.class));
    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }
}
