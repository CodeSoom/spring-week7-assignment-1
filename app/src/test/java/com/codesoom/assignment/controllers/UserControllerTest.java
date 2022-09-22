package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(UserController.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;


    @Nested
    @DisplayName("update()")
    class Describe_Update{

        @Nested
        @DisplayName("식별자에 해당하는 사용자가 존재하고 인증을 통과한다면")
        class Context_ExistedIdAndValidToken{

            @Nested
            @DisplayName("수정자와 등록자가 서로 다르다면")
            class Context_ModifierNotEqualsRegister{

                private Long registeredId;

                private String modifierContent;
                private String modifierToken;

                @BeforeEach
                void setUp() throws JsonProcessingException {
                    UserRegistrationData registrationData = new UserRegistrationData("register@google.com", "register", "register");
                    registeredId = userService.registerUser(registrationData).getId();

                    UserModificationData modificationData = new UserModificationData("modifier", "modifier");
                    modifierContent = objectMapper.writeValueAsString(modificationData);
                    modifierToken = jwtUtil.encode(registeredId + 1L);
                }

                @AfterEach
                void tearDown() {
                    userService.deleteUser(registeredId);
                }

                @Test
                @DisplayName("인증 자격 증명이 부족하다는 상태를 반환하고 예외를 던진다.")
                void It_ThrowException() throws Exception {
                    ResultActions result = mockMvc.perform(patch("/users/" + registeredId)
                            .content(modifierContent)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + modifierToken));
                    result.andDo(print())
                            .andExpect(status().isUnauthorized())
                            .andExpect(handler().handlerType(UserController.class))
                            .andExpect(handler().methodName("update"));
                }
            }
        }
    }
}
