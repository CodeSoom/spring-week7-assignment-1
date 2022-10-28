package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAuthenticateControllerTest {
    private String invalidToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String validToken;

    @DisplayName("update 메서드")
    @Nested
    class Describe_update {
        private User savedUser;
        private String requestBody;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            savedUser = jpaUserRepository.save(User.builder()
                    .email("a@a.com")
                    .name("김코드")
                    .password(passwordEncoder.encode("12345"))
                    .build()
            );

            UserModificationData userModificationData = UserModificationData.builder()
                    .name("김 숨")
                    .password("098765")
                    .build();

            requestBody = objectMapper.writeValueAsString(userModificationData);

            validToken = "Bearer " + authenticationService.login(savedUser.getEmail(), "12345");
            invalidToken = validToken + "0";
        }

        @AfterEach
        void tearDown() {
            jpaUserRepository.deleteById(savedUser.getId());
        }

        @DisplayName("유효하지 않은 token 으로 요청한 경우")
        @Nested
        class Context_with_invalid_token {
            @DisplayName("401을 응답한다")
            @Test
            void it_returns_401() throws Exception {
                mockMvc.perform(patch("/users/" + savedUser.getId())
                                .header("Authorization", invalidToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @DisplayName("유효한 token 으로 요청한 경우")
        @Nested
        class Context_with_valid_token {
            @DisplayName("200을 응답한다")
            @Test
            void it_returns_200() throws Exception {
                mockMvc.perform(patch("/users/" + savedUser.getId())
                                .header("Authorization", validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                        )
                        .andExpect(status().isOk());
            }
        }

        @DisplayName("다른 유저가 요청한 경우")
        @Nested
        class Context_with_valid_token_but_different_user {
            private String savedUser2Token;

            @BeforeEach
            void setUp() {
                User savedUser2 = jpaUserRepository.save(User.builder()
                        .email("b@b.com")
                        .name("김b")
                        .password(passwordEncoder.encode("12345"))
                        .build()
                );
                savedUser2Token = "Bearer " + authenticationService.login(savedUser2.getEmail(), "12345");
            }

            @DisplayName("403을 응답한다")
            @Test
            void it_returns_403() throws Exception {
                mockMvc.perform(patch("/users/" + savedUser.getId())
                                .header("Authorization", savedUser2Token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                        )
                        .andExpect(status().isForbidden());
            }
        }
    }
}
