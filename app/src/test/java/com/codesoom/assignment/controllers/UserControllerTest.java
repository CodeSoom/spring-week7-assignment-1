package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.utils.JwtUtil;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private final String REGISTER = "register";
    private final String MODIFIER = "modifier";

    private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOi0xfQ.QJ8Lac_HWq1-tmNfZ9iV4Hewi-sWssOuKHpK4fhUaw0";
    private static final String USERID_1_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String USERID_2_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjJ9.TEM6MULsZeqkBbUKziCR4Dg_8kymmZkyxsCXlfNJ3g0";

    private UserModificationData newUserModificationData(String name , String password){
        return new UserModificationData(name, password);
    }

    private UserRegistrationData newUserRegistrationData(String email , String name , String password){
        return new UserRegistrationData(email , name , password);
    }

    @Nested
    @DisplayName("create()")
    class Describe_Create{

        @Nested
        @DisplayName("인증을 통과한다면")
        class Context_ValidToken{

            private final Long registeredId = 1L;
            private String registrationContent;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                final UserRegistrationData registrationData = newUserRegistrationData(REGISTER, REGISTER, REGISTER);
                registrationContent = objectMapper.writeValueAsString(registrationData);
            }

            @Test
            @DisplayName("상품을 생성하고 자원을 생성했다는 상태 코드와 생성된 상품을 반환한다.")
            void It_() throws Exception {
                final ResultActions result = mockMvc.perform(patch("/users/" + registeredId)
                        .content(registrationContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + USERID_1_TOKEN));
                result.andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(handler().handlerType(UserController.class))
                        .andExpect(handler().methodName("create"))
                        .andExpect(jsonPath("$.response.email" , is(REGISTER)))
                        .andExpect(jsonPath("$.response.name" , is(REGISTER)))
                        .andExpect(jsonPath("$.response.password" , is(REGISTER)));
            }
        }
    }


    @Nested
    @DisplayName("update()")
    class Describe_Update{

        @Nested
        @DisplayName("식별자에 해당하는 사용자가 존재한다면")
        class Context_ExistedUser{
            private Long registeredId;

            @BeforeEach
            void setUp() {
                final UserRegistrationData registrationData = new UserRegistrationData("register@google.com", "register", "register");
                registeredId = userService.registerUser(registrationData).getId();
            }

            @AfterEach
            void tearDown() {
                userService.deleteUser(registeredId);
            }

            @Nested
            @DisplayName("인증을 통과한다면")
            class Context_ValidToken{
                private String modifierToken;

                @BeforeEach
                void setUp() {
                    modifierToken = jwtUtil.encode(registeredId + 1L);
                }

                @Nested
                @DisplayName("등록자는 아니지만 관리자라면")
                class Context_RoleAdmin{

                    private String modifierContent;

                    @BeforeEach
                    void setUp() throws JsonProcessingException {
                        final UserModificationData modificationData = newUserModificationData(MODIFIER , MODIFIER);
                        modifierContent = objectMapper.writeValueAsString(modificationData);
                    }

                    @Test
                    @DisplayName("수정한다.")
                    void It_Update() throws Exception {
                        final ResultActions result = mockMvc.perform(patch("/users/" + registeredId)
                                .content(modifierContent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + modifierToken));
                        result.andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(handler().handlerType(UserController.class))
                                .andExpect(handler().methodName("update"))
                                .andExpect(jsonPath("$.response.name" , is(MODIFIER)))
                                .andExpect(jsonPath("$.response.password" , is(MODIFIER)));
                    }
                }

                @Nested
                @DisplayName("수정자와 등록자가 서로 다르다면")
                class Context_ModifierNotEqualsRegister{

                    private String modifierContent;

                    @BeforeEach
                    void setUp() throws JsonProcessingException {
                        final UserModificationData modificationData = newUserModificationData(MODIFIER , MODIFIER);
                        modifierContent = objectMapper.writeValueAsString(modificationData);
                    }

                    @Test
                    @DisplayName("접근을 거부한다는 상태를 반환하고 예외를 던진다.")
                    void It_ThrowException() throws Exception {
                        final ResultActions result = mockMvc.perform(patch("/users/" + registeredId)
                                .content(modifierContent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + modifierToken));
                        result.andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(handler().handlerType(UserController.class))
                                .andExpect(handler().methodName("update"));
                    }
                }

            }
        }
    }
}
