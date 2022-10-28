package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.mapper.UserFactory;
import com.codesoom.assignment.security.JwtTokenProvider;
import com.codesoom.assignment.utils.UserSampleFactory;
import com.codesoom.assignment.utils.UserSampleFactory.FieldName;
import com.codesoom.assignment.utils.UserSampleFactory.ValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController 클래스")
class UserControllerTest {
    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        // ResponseBody JSON에 한글이 깨지는 문제로 추가
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
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
        @DisplayName("필수항목에 빈 값이 주어지면")
        class Context_with_blank_value {
            private final List<UserDto.RegisterParam> givenUsers = new ArrayList<>();

            @BeforeEach
            void prepare() {
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.EMAIL, ValueType.NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.EMAIL, ValueType.EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.NAME, ValueType.NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.NAME, ValueType.EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.NAME, ValueType.WHITESPACE));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.PASSWORD, ValueType.NULL));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.PASSWORD, ValueType.EMPTY));
                givenUsers.add(UserSampleFactory.createRequestParamWith(FieldName.PASSWORD, ValueType.WHITESPACE));
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
        @DisplayName("패스워드 길이가 13자 미만이면")
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
                        .andExpect(content().string(containsString("size must be between 13 and 1024")))
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
}
