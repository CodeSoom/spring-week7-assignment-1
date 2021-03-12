package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationGuard;
import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.AccessDeniedException;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String USER1_VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    private static final Long USER2 = 2L;
    public static final Long USER1 = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContext securityContext;

    @MockBean(name = "authenticationGuard")
    private AuthenticationGuard authenticationGuard;

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

        User user = User.builder()
                .id(1L)
                .email("tester@example.com")
                .name("TEST")
                .build();

        given(userService.updateUser(
                eq(1L), any(UserModificationData.class)))
                .willReturn(user);

        given(userService.updateUser(
                eq(100L), any(UserModificationData.class)))
                .willThrow(new UserNotFoundException(100L));

        given(userService.deleteUser(100L))
                .willThrow(new UserNotFoundException(100L));

        SecurityContextHolder.setContext(securityContext);

        given(authenticationService.parseToken(VALID_TOKEN))
                .willReturn(1L);

        given(authenticationService.parseToken(INVALID_TOKEN))
                .willThrow(new InvalidTokenException(INVALID_TOKEN));

        given(userService.updateUser(
                eq(USER2), any(UserModificationData.class)))
                .willThrow(new AccessDeniedException(USER2));
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

    @Test
    void updateUserWithValidAttributes() throws Exception {
        given(authenticationGuard.checkIdMatch(any(Authentication.class), eq(USER1)))
                .willReturn(true);

        mockMvc.perform(
                patch("/users/" + USER1)
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
    }

    @Test
    void updateUserWithInValidAttributes() throws Exception {
        given(authenticationGuard.checkIdMatch(any(Authentication.class), eq(USER1)))
                .willReturn(true);

        mockMvc.perform(
                patch("/users/" + USER1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"1\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithInvalidToken() throws Exception {
        mockMvc.perform(
                patch("/users/" + USER1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserWithAccessDeniedToken() throws Exception {
        mockMvc.perform(
                patch("/users/" + USER2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                        .header("Authorization", "Bearer " + USER1_VALID_TOKEN)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserWithInvalidAttributes() throws Exception {
        given(authenticationGuard.checkIdMatch(any(Authentication.class), eq(USER1)))
                .willReturn(true);

        mockMvc.perform(
                patch("/users/" + USER1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"password\":\"\"}")
                        .header("Authorization", "Bearer " + USER1_VALID_TOKEN)

        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(delete("/users/" + USER1))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }

    @Test
    void registerUserWithExistedEmail() throws Exception {
        given(userService.registerUser(any(UserRegistrationData.class)))
                .willThrow(new UserEmailDuplicationException("tester@example.com"));

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"tester@example.com\"," +
                                "\"name\":\"Tester\",\"password\":\"test\"}")
        )
                .andExpect(status().isBadRequest());
    }
}
