package com.codesoom.assignment.controllers;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.security.JwtTokenProvider;
import com.codesoom.assignment.utils.UserSampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.EMAIL;
import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.NAME;
import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.PASSWORD;
import static com.codesoom.assignment.utils.UserSampleFactory.ValueType.EMPTY;
import static com.codesoom.assignment.utils.UserSampleFactory.ValueType.NULL;
import static com.codesoom.assignment.utils.UserSampleFactory.ValueType.WHITESPACE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController 클래스")
class UserControllerTest {
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        // ResponseBody JSON에 한글이 깨지는 문제로 추가
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Nested
    @DisplayName("createUser[/users::POST] 메소드는")
    class Describe_createUser {
        ResultActions subject(UserDto.RegisterParam request) throws Exception {
            return mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }
        @Nested
        @DisplayName("유효한 사용자 정보가 주어지면")
        class Context_with_valid_user_info {
            private final UserDto.RegisterParam givenUser = UserSampleFactory.createRequestParam();

            @Test
            @DisplayName("CREATED(201)와 등록된 회원정보를 리턴한다")
            void it_returns_201_and_registered_user() throws Exception {
                final ResultActions resultActions = subject(givenUser);

                resultActions.andExpect(status().isCreated())
                        .andExpect(jsonPath("email").value(equalTo(givenUser.getEmail())))
                        .andExpect(jsonPath("name").value(equalTo(givenUser.getName())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("중복된 이메일이 주어지면")
        class Context_with_duplicate_email_ {
            private final UserDto.RegisterParam givenUser = UserSampleFactory.createRequestParam();

            @BeforeEach
            void prepare() {
                final User savedUser = userRepository.save(UserSampleFactory.createUser());
                givenUser.setEmail(savedUser.getEmail());
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenUser);

                resultActions.andExpect(status().isBadRequest())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("필수항목에 빈 값이 주어지면")
        class Context_with_blank_value {
            private final List<UserDto.RegisterParam> givenUsers = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenUsers.add(UserSampleFactory.createRequestParamWith(EMAIL, NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(EMAIL, EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(NAME, NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(NAME, EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(NAME, WHITESPACE));
                givenUsers.add(UserSampleFactory.createRequestParamWith(PASSWORD, NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(PASSWORD, EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(PASSWORD, WHITESPACE));
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                givenUsers.forEach(this::test);
            }

            private void test(UserDto.RegisterParam givenUser) {
                try {
                    final ResultActions resultActions = subject(givenUser);

                    resultActions.andExpect(status().isBadRequest())
                            .andDo(print());
                } catch (Exception e) {}
            }
        }

        @Nested
        @DisplayName("유효하지않은 이메일 형식이 주어지면")
        class Context_with_invalid_email_format {
            private final UserDto.RegisterParam givenUser = UserSampleFactory.createRequestParam();

            @BeforeEach
            void prepare() {
                givenUser.setEmail("@invalid.email");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenUser);

                resultActions.andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("well-formed email address")))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("패스워드 길이가 8자 미만이면")
        class Context_with_password_length_under_13_chars {
            private final UserDto.RegisterParam givenUser = UserSampleFactory.createRequestParam();

            @BeforeEach
            void prepare() {
                givenUser.setPassword("abcde");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenUser);

                resultActions.andExpect(status().isBadRequest())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("패스워드에 같은문자가 3번이상 반복되면")
        class Context_with_password_consecutive_same_chars {
            private final UserDto.RegisterParam givenUser = UserSampleFactory.createRequestParam();

            @BeforeEach
            void prepare() {
                givenUser.setPassword("invaliddddpassowrd");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenUser);

                resultActions.andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("같은 문자가 3번이상 반복되면 안됩니다.")))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("updateUser[/users/id::PATCH] 메소드는")
    class Describe_updateUser {
        private final User savedUser = userRepository.save(UserSampleFactory.createUser());
        private final Long EXIST_USER_ID = savedUser.getId();
        private final String VALID_TOKEN = jwtTokenProvider.createToken(EXIST_USER_ID);

        ResultActions subject(Long id, UserDto.UpdateParam request, String accessToken) throws Exception {
            return mockMvc.perform(patch("/users/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", "Bearer " + accessToken));
        }

        @Nested
        @DisplayName("수정된 회원정보가 주어지면")
        class Context_with_modified_user_info {
            private final UserDto.UpdateParam givenRequest = new UserDto.UpdateParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedUser.getName());
                givenRequest.setPassword("modified_" + savedUser.getPassword());
            }

            @Test
            @DisplayName("OK(200)와 수정된 상품을 리턴한다")
            void it_returns_200_and_modified_user() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, givenRequest, VALID_TOKEN);

                resultActions.andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("name").value(equalTo(givenRequest.getName())))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("필수항목에 빈 값이 주어지면")
        class Context_with_blank_value {
            private final List<UserDto.UpdateParam> givenUsers = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenUsers.add(UserSampleFactory.createUpdateParamWith(NAME, NULL));
                givenUsers.add(UserSampleFactory.createUpdateParamWith(NAME, EMPTY));
                givenUsers.add(UserSampleFactory.createUpdateParamWith(NAME, WHITESPACE));
                givenUsers.add(UserSampleFactory.createUpdateParamWith(PASSWORD, NULL));
                givenUsers.add(UserSampleFactory.createUpdateParamWith(PASSWORD, EMPTY));
                givenUsers.add(UserSampleFactory.createUpdateParamWith(PASSWORD, WHITESPACE));
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다.")
            void it_returns_400_and_error_message() throws Exception {
                givenUsers.forEach(this::test);
            }

            private void test(UserDto.UpdateParam givenUser) {
                try {
                    ResultActions resultActions = subject(EXIST_USER_ID, givenUser, VALID_TOKEN);

                    resultActions.andExpect(status().isBadRequest())
                            .andDo(print());
                } catch (Exception e) {
                }
            }
        }


        @Nested
        @DisplayName("패스워드 길이가 8자 미만이면")
        class Context_with_password_length_under_13_chars {
            private final UserDto.UpdateParam givenUser = UserSampleFactory.createUpdateParam();

            @BeforeEach
            void prepare() {
                givenUser.setPassword("abcde");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, givenUser, VALID_TOKEN);

                resultActions.andExpect(status().isBadRequest())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("패스워드에 같은문자가 3번이상 반복되면")
        class Context_with_password_consecutive_same_chars {
            private final UserDto.UpdateParam givenUser = UserSampleFactory.createUpdateParam();

            @BeforeEach
            void prepare() {
                givenUser.setPassword("invaliddddpassowrd");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, givenUser, VALID_TOKEN);

                resultActions.andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("같은 문자가 3번이상 반복되면 안됩니다.")))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("다른사람의 회원정보를 수정하면")
        class Context_with_other_user_info {
            private final UserDto.UpdateParam givenUser = UserSampleFactory.createUpdateParam();
            private final User otherUser = userRepository.save(UserSampleFactory.createUser());

            @Test
            @DisplayName("FORBIDDEN(403)을 리턴한다")
            void it_returns_403() throws Exception {
                final ResultActions resultActions = subject(otherUser.getId(), givenUser, VALID_TOKEN);

                resultActions.andExpect(status().isForbidden())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_empty_token {
            private final UserDto.UpdateParam givenRequest = new UserDto.UpdateParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedUser.getName());
                givenRequest.setPassword("modifyPassword");
            }

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = mockMvc.perform(patch("/users/{id}", EXIST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)));

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("잘못된 토큰이 주어지면")
        class Context_with_wrong_token {
            private final UserDto.UpdateParam givenRequest = new UserDto.UpdateParam();

            @BeforeEach
            void prepare() {
                givenRequest.setName("수정_" + savedUser.getName());
                givenRequest.setPassword("modifyPassword");
            }

            @Test
            @DisplayName("UNAUTHORIZED(401)을 리턴한다.")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, givenRequest, INVALID_TOKEN);

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("deleteUser[/users/id::DELETE] 메소드는")
    class Describe_deleteUser {
        private final User givenUser = userRepository.save(UserSampleFactory.createUser());
        private final Long EXIST_USER_ID = givenUser.getId();
        private final String VALID_TOKEN = jwtTokenProvider.createToken(EXIST_USER_ID);
        ResultActions subject(Long id, String accessToken) throws Exception {
            return mockMvc.perform(delete("/users/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken));
        }

        @Nested
        @DisplayName("유효한 회원ID가 주어지면")
        class Context_with_valid_user_id {
            @Test
            @DisplayName("상품을 삭제하고 NO_CONTENT(204)를 리턴한다")
            void it_returns_204() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, VALID_TOKEN);

                resultActions.andExpect(status().isNoContent())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않으면")
        class Context_with_empty_token {
            @Test
            @DisplayName("UNAUTHORIZED(401)를 리턴한다")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = mockMvc.perform(delete("/users/" + EXIST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON));

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("잘못된 토근이 주어지면")
        class Context_with_wrong_token {
            @Test
            @DisplayName("UNAUTHORIZED(401)를 리턴한다")
            void it_returns_401() throws Exception {
                final ResultActions resultActions = subject(EXIST_USER_ID, INVALID_TOKEN);

                resultActions.andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id {
            private final Long INVALID_USER_ID = 9999L;

            @Test
            @DisplayName("NOT_FOUND(404)와 예외 메시지를 리턴한다")
            void it_returns_404_and_message() throws Exception {
                final ResultActions resultActions = subject(INVALID_USER_ID, VALID_TOKEN);

                resultActions.andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("message", containsString("User not found")))
                        .andDo(print());
            }
        }
    }
}
