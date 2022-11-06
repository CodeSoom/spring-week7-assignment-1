package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionControllerTest {

    private static final String EMAIL = "tester@example.com";
    private static final String PASSWORD = "TEST1234";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void clear() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("login 메소드는")
    class Describe_login {

        @Nested
        @DisplayName("유효한 회원 정보가 주어지면")
        class Context_with_valid_user_info {
            private final SessionRequestData sessionRequest = new SessionRequestData(EMAIL, PASSWORD);
            private Long userId;

            @BeforeEach
            void setUp() {
                User user = userRepository.save(getUser());
                userId = user.getId();
            }

            @Test
            @DisplayName("201 응답을 생성한다")
            void it_creates_created() throws Exception {
                mockMvc.perform(post("/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sessionRequest)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken").value(jwtUtil.encode(userId)));
            }
        }

        @Nested
        @DisplayName("잘못된 이메일이나 비밀번호가 주어지면")
        class Context_with_wrong_email_or_password {
            private final SessionRequestData wrongPassword
                    = new SessionRequestData(EMAIL, "wrong" + PASSWORD);

            private final SessionRequestData wrongEmail
                    = new SessionRequestData("wrong" + EMAIL, PASSWORD);

            @BeforeEach
            void setUp() {
                userRepository.save(getUser());
            }

            @Test
            @DisplayName("400 응답을 생성한다")
            void it_creates_bad_request() throws Exception {
                mockMvc.perform(post("/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wrongPassword)))
                        .andExpect(status().isBadRequest());

                mockMvc.perform(post("/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wrongEmail)))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    private User getUser() {
        return User.builder()
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .build();
    }
}
