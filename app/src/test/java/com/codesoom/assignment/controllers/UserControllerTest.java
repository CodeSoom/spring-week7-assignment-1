package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TestUtils;
import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.dto.UserLoginData;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private static final String EXIST_EMAIL = "test@test.com";
    private static final String NOT_EXIST_EMAIL = "bad@test.com";

    private static final String PASSWORD = "password";
    private static final String WRONG_PASSWORD = "xxxx";

    private static final String NAME = "name";
    private static final String MODIFY_NAME = "codesoom";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private WebApplicationContext wac;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserRegistrationData userRegistrationData;
    private UserRegistrationData invalidUserRegistrationData;
    private UserModificationData userModificationData;
    private UserModificationData invalidUserModificationData;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        userRegistrationData = UserRegistrationData.builder()
                .name(NAME)
                .email(EXIST_EMAIL)
                .password(PASSWORD)
                .build();

        invalidUserRegistrationData = UserRegistrationData.builder()
                .name("")
                .email(NOT_EXIST_EMAIL)
                .password(PASSWORD)
                .build();

        userModificationData = UserModificationData.builder()
                .name(MODIFY_NAME)
                .password(PASSWORD)
                .build();

        invalidUserModificationData = UserModificationData.builder()
                .name("")
                .password(PASSWORD)
                .build();
    }

    @Nested
    @DisplayName("회원 생성 요청시")
    class Describe_post {
        @Nested
        @DisplayName("올바른 사용자 정보가 주어진다면")
        class Context_with_valid_user {
            @Test
            @DisplayName("회원정보와 Created HTTP 상태코드를 응답한다.")
            void it_return_user_created() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userRegistrationData))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name", is(NAME)))
                        .andExpect(jsonPath("$.email", is(EXIST_EMAIL)))
                        .andDo(print());

            }
        }

        @Nested
        @DisplayName("유효하지 않은 사용자 정보가 주어진다면")
        class Context_with_invalid_user {

            @Test
            @DisplayName("Bad Request HTTP 상태코드를 응답한다.")
            void it_return_user_BadRequest() throws Exception {
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidUserRegistrationData))
                        )
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("회원 수정 요청시")
    class Describe_patch {
        @Nested
        @DisplayName("올바른 사용자 정보가 주어진다면")
        class Context_with_valid_user {
            private User user;
            private UserLoginData userLoginData;
            private UserResultData userResultData;
            private SessionResponseData sessionResponseData;

            @BeforeEach
            void setUp() throws Exception {
                user = User.builder()
                        .name(NAME)
                        .email(EXIST_EMAIL)
                        .password(PASSWORD)
                        .build();
                userLoginData = UserLoginData.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .build();

                userResultData = createUser(user);
                sessionResponseData = login(userLoginData);
            }

            @Test
            @DisplayName("수정된 회원정보와 OK HTTP 상태코드를 응답한다.")
            void it_return_user_status_ok() throws Exception {
                mockMvc.perform(
                                patch("/users/" + user.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userModificationData))
                                        .header("Authorization",
                                                "Bearer " + sessionResponseData.getAccessToken()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(userResultData.getId())))
                        .andExpect(jsonPath("$.name", is(userModificationData.getName())))
                ;
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

    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(delete("/users/1")
                )
                .andExpect(status().isNoContent());

    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());

    }

    private UserResultData createUser(User user)
            throws Exception {
        UserRegistrationData userRegistrationData = UserRegistrationData
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .build();

        ResultActions actions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationData)));

        return TestUtils.content(actions, UserResultData.class);
    }

    private SessionResponseData login(UserLoginData user) throws Exception {
        SessionRequestData sessionRequestData = SessionRequestData
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ResultActions actions = mockMvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequestData)));

        return TestUtils.content(actions, SessionResponseData.class);
    }
}

