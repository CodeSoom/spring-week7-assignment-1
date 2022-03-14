package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TestUtils;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController 테스트")
class UserControllerTest {
    private static final String SECRET = "12345678901234567890123456789010";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    private SessionResponseData login(String email, String password) throws Exception {
        SessionRequestData sessionRequestData = SessionRequestData.builder()
                .email(email)
                .password(password)
                .build();

        String content = objectMapper.writeValueAsString(sessionRequestData);

        ResultActions actions = mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        return TestUtils.content(actions, SessionResponseData.class);
    }

    private UserResultData createUser(String email, String name, String password) throws Exception {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        ResultActions actions = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))
        );

        return TestUtils.content(actions, UserResultData.class);
    }

    @AfterEach
    void deleteAllUserRepository() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /users 요청은")
    class Describe_POST_users {

        @Nested
        @DisplayName("가입하려는 회원 정보가 있다면")
        class Context_existed_registration_data {

            private String requestContent;

            @BeforeEach
            void prepare() throws JsonProcessingException {
                UserRegistrationData registrationData = UserRegistrationData.builder()
                        .name("곽형조")
                        .email("test@example.com")
                        .password("test1234")
                        .build();

                requestContent = objectMapper.writeValueAsString(registrationData);
            }

            @Test
            @DisplayName("회원 가입을 하고, 가입한 회원의 정보를 응답한다")
            void it_response_user() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestContent)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("name").value("곽형조"))
                        .andExpect(jsonPath("email").value("test@example.com"));
            }
        }

        @Nested
        @DisplayName("가입 정보 중 필수 정보가 빠져있다면")
        class Context_without_required_data {

            private String requestContent;

            @BeforeEach
            void prepare() throws JsonProcessingException {
                UserRegistrationData registrationData = UserRegistrationData.builder()
                        .name("곽형조")
                        .password("test1234")
                        .build();

                requestContent = objectMapper.writeValueAsString(registrationData);
            }

            @Test
            @DisplayName("Bad request 를 응답한다")
            void it_response_bad_request() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestContent)
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("이미 존재하는 이메일로 가입한다면")
        class Context_with_duplicated_email {

            private static final String DUPLICATED_EMAIL = "test@example.com";
            private String requestContent;

            @BeforeEach
            void prepare() throws JsonProcessingException {
                User user = User.builder()
                        .id(1L)
                        .name("곽형조")
                        .email(DUPLICATED_EMAIL)
                        .password("test1234")
                        .deleted(false)
                        .build();

                userRepository.save(user);

                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .name("홍길동")
                        .email(DUPLICATED_EMAIL)
                        .password("asdqwe1234")
                        .build();

                requestContent = objectMapper.writeValueAsString(userRegistrationData);
            }

            @Test
            @DisplayName("Bad request 를 응답한다")
            void it_response_bad_request() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestContent)
                        )
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("PATCH /users/{id} 요청은")
    class Describe_PATCH_users_id {

        private UserResultData user;
        private SessionResponseData session;

        @Nested
        @DisplayName("Access token 이 존재하고")
        class Context_with_valid_access_token {

            @BeforeEach
            void prepare() throws Exception {
                user = createUser("test@example.com", "곽형조", "asdqwe1234");
                session = login(user.getEmail(), "asdqwe1234");
            }

            @Nested
            @DisplayName("수정된 회원 정보가 주어진다면")
            class Context_with_modification_user_data {

                private String requestContent;

                @BeforeEach
                void prepareUserAndAccessToken() throws Exception {
                    UserModificationData modificationData = UserModificationData.builder()
                            .name("수정된이름")
                            .password("수정된비밀번호")
                            .build();

                    requestContent = objectMapper.writeValueAsString(modificationData);
                }

                @Test
                @DisplayName("수정된 회원 정보를 응답한다")
                void it_response_modified_user() throws Exception {
                    mockMvc.perform(
                                    patch("/users/1")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(requestContent)
                                            .header(
                                                    "Authorization",
                                                    String.format("Bearer %s", session.getAccessToken())
                                            )
                            )
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("name").value("수정된이름"))
                            .andExpect(jsonPath("password").value("수정된비밀번호"));

                }
            }
        }

    }

//    @BeforeEach
//    void setUp() {
//        given(userService.registerUser(any(UserRegistrationData.class)))
//                .will(invocation -> {
//                    UserRegistrationData registrationData = invocation.getArgument(0);
//                    return User.builder()
//                            .id(13L)
//                            .email(registrationData.getEmail())
//                            .name(registrationData.getName())
//                            .build();
//                });
//
//
//        given(userService.updateUser(eq(1L), any(UserModificationData.class)))
//                .will(invocation -> {
//                    Long id = invocation.getArgument(0);
//                    UserModificationData modificationData =
//                            invocation.getArgument(1);
//                    return User.builder()
//                            .id(id)
//                            .email("tester@example.com")
//                            .name(modificationData.getName())
//                            .build();
//                });
//
//        given(userService.updateUser(eq(100L), any(UserModificationData.class)))
//                .willThrow(new UserNotFoundException(100L));
//
//        given(userService.deleteUser(100L))
//                .willThrow(new UserNotFoundException(100L));
//    }
//    @Test
//    void updateUserWithValidAttributes() throws Exception {
//        mockMvc.perform(
//                        patch("/users/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("{\"name\":\"TEST\",\"password\":\"test\"}")
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string(
//                        containsString("\"id\":1")
//                ))
//                .andExpect(content().string(
//                        containsString("\"name\":\"TEST\"")
//                ));
//
//        verify(userService).updateUser(eq(1L), any(UserModificationData.class));
//    }
//
//    @Test
//    void updateUserWithInvalidAttributes() throws Exception {
//        mockMvc.perform(
//                        patch("/users/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("{\"name\":\"\",\"password\":\"\"}")
//                )
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateUserWithNotExsitedId() throws Exception {
//        mockMvc.perform(
//                        patch("/users/100")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
//                )
//                .andExpect(status().isNotFound());
//
//        verify(userService)
//                .updateUser(eq(100L), any(UserModificationData.class));
//    }
//
//    @Test
//    void destroyWithExistedId() throws Exception {
//        mockMvc.perform(delete("/users/1"))
//                .andExpect(status().isNoContent());
//
//        verify(userService).deleteUser(1L);
//    }
//
//    @Test
//    void destroyWithNotExistedId() throws Exception {
//        mockMvc.perform(delete("/users/100"))
//                .andExpect(status().isNotFound());
//
//        verify(userService).deleteUser(100L);
//    }
}
