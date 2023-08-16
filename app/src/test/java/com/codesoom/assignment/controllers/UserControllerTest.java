package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

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

    private static final Long MY_ID = 1L;
    private static final Long OTHER_ID = 2L;
    private static final Long ADMIN_ID = 1000L;
    private static final Long NOT_EXISTING_ID = 9999L;
    private static final String MY_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String OTHER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjJ9.TEM6MULsZeqkBbUKziCR4Dg_8kymmZkyxsCXlfNJ3g0";
    private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEwMDB9.nnjhgy2R3Qo48tUtI-ib-D-Aqjfz4338xMhAHg2OFxA";
    private static final String INVALID_TOKEN = "ABChbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    @BeforeEach
    void setUp() {
        given(userService.registerUser(any(UserRegistrationData.class)))
                .will(invocation -> {
                    UserRegistrationData registrationData = invocation.getArgument(0);
                    return User.builder()
                            .id(MY_ID)
                            .email(registrationData.getEmail())
                            .name(registrationData.getName())
                            .build();
                });

        given(userService.updateUser(eq(MY_ID), any(UserModificationData.class)))
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

        given(userService.deleteUser(NOT_EXISTING_ID)).willThrow(UserNotFoundException.class);

        given(authenticationService.parseToken(MY_TOKEN)).willReturn(MY_ID);
        given(authenticationService.parseToken(OTHER_TOKEN)).willReturn(OTHER_ID);
        given(authenticationService.parseToken(ADMIN_TOKEN)).willReturn(ADMIN_ID);
        given(authenticationService.parseToken(INVALID_TOKEN))
                .willThrow(new InvalidTokenException(INVALID_TOKEN));

        given(authenticationService.getUserRoles(MY_ID)).willReturn(Collections.singletonList("USER"));
        given(authenticationService.getUserRoles(OTHER_ID)).willReturn(Collections.singletonList("USER"));
        given(authenticationService.getUserRoles(ADMIN_ID)).willReturn(Collections.singletonList("ADMIN"));

    }

    @Test
    void registerUserWithValidAttributes() throws Exception {
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"tester@example.com\"," +
                                "\"name\":\"Tester\",\"password\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"id\":" + MY_ID)))
                .andExpect(content().string(containsString("\"email\":\"tester@example.com\"")))
                .andExpect(content().string(containsString("\"name\":\"Tester\"")));

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

    @Test
    void updateUserWithValidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/" + MY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                        .header("Authorization", "Bearer " + MY_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":1")))
                .andExpect(content().string(containsString("\"name\":\"TEST\"")));

        verify(userService).updateUser(eq(1L), any(UserModificationData.class));
    }

    @Test
    void updateUserWithInvalidAttributes() throws Exception {
        mockMvc.perform(
                patch("/users/"+MY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"\"}")
                        .header("Authorization", "Bearer " + MY_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithoutAccessToken() throws Exception {
        mockMvc.perform(
                patch("/users/"+MY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"TEST\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserWithNotMyAccessToken() throws Exception {
        mockMvc.perform(
                        patch("/users/"+MY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                                .header("Authorization", "Bearer " + OTHER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void destroyWithoutAccessToken() throws Exception {
        mockMvc.perform(delete("/users/"+MY_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void destroyWithNotExistingUser() throws Exception {
        mockMvc.perform(delete("/users/" + NOT_EXISTING_ID)
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }
}
