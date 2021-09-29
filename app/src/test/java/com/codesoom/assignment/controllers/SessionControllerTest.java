package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaUserRepository userRepository;

    private static final String JWT_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private SessionRequestData sessionRequestData;
    private User userFixture;

    @BeforeEach
    void setupFixture() {
        userFixture = User.builder()
                .name("nana")
                .email("test@email.com")
                .password("password")
                .build();
        userFixture.changePassword("password", new BCryptPasswordEncoder());
    }

    @Nested
    @DisplayName("사용자가 존재하고 유효한 비밀번호를 입력한 경우")
    class WithExistentUserAndValidPassword {
        @BeforeEach
        void setup() {
            given(userRepository.findByEmail("test@email.com"))
                    .willReturn(Optional.of(userFixture));

            sessionRequestData = SessionRequestData.builder()
                    .email("test@email.com")
                    .password("password")
                    .build();
        }

        @Test
        @DisplayName("토큰과 201 Created 상태코드로 응답한다.")
        void responsesWithToken() throws Exception {
            mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sessionRequestData)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken", matchesPattern(JWT_REGEX)));
        }
    }

    @Nested
    @DisplayName("사용자는 존재하나 유효하지 않은 비밀번호를 입력한 경우")
    class WithInvalidPassword {
        @BeforeEach
        void setup() throws Exception {
            given(userRepository.findByEmail("test@email.com"))
                    .willReturn(Optional.of(userFixture));

            sessionRequestData = SessionRequestData.builder()
                    .email("test@email.com")
                    .password("wrong-password")
                    .build();
        }

        @Test
        @DisplayName("400 Bad Request 에러로 응답한다.")
        void responseWithBadRequestError() throws Exception {
            mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sessionRequestData)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("사용자가 존재하지 않는 경우")
    class WithNonExistentUser {
        @BeforeEach
        void setup() {
            given(userRepository.findByEmail("non-existent-user@email.com"))
                    .willThrow(new LoginFailException("non-existent-user@email.com"));

            sessionRequestData = SessionRequestData.builder()
                    .email("non-existent-user@email.com")
                    .password("password")
                    .build();
        }

        @Test
        @DisplayName("400 Bad Request 에러로 응답한다.")
        void responsesWithNotFoundError() throws Exception {
            mockMvc.perform(post("/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sessionRequestData)))
                    .andExpect(status().isBadRequest());
        }
    }
}
