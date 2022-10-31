package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionDto;
import com.codesoom.assignment.mapper.UserFactory;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionController 클래스")
class SessionControllerTest {
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

    @BeforeEach
    void setup() {
        // ResponseBody JSON에 한글이 깨지는 문제로 추가
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Nested
    @DisplayName("login[/session::POST] 메소드는")
    class Describe_registerUser {
        ResultActions subject(SessionDto.SessionRequestData request) throws Exception {
            return mockMvc.perform(post("/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        @Nested
        @DisplayName("유효한 로그인정보가 주어지면")
        class Context_with_login_info {
            private SessionDto.SessionRequestData givenRequest = new SessionDto.SessionRequestData();
            @BeforeEach
            void prepare() {
                UserCommand.Register command = userFactory.of(UserSampleFactory.createRequestParam());
                User savedUser = userService.registerUser(command);
                givenRequest.setEmail(command.getEmail());
                givenRequest.setPassword(command.getPassword());
            }

            @Test
            @DisplayName("CREATED(201)와 액세스 토큰을 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenRequest);

                resultActions.andExpect(status().isCreated())
                        .andExpect(content().string(containsString(".")))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("존재하지않는 이메일이 주어지면")
        class Context_with_not_existed_email {
            private SessionDto.SessionRequestData givenRequest = new SessionDto.SessionRequestData();
            @BeforeEach
            void prepare() {
                givenRequest.setEmail("invalid_email@test.com");
                givenRequest.setPassword("test");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러 메세지를 리턴한다")
            void it_returns_400_and_error_message() throws Exception {
                final ResultActions resultActions = subject(givenRequest);

                resultActions.andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("failed")))
                        .andDo(print());
            }
        }


        @Nested
        @DisplayName("잘못된 비밀번호가 주어지면")
        class Context_with_wrong_password {
            private SessionDto.SessionRequestData givenRequest = new SessionDto.SessionRequestData();
            @BeforeEach
            void prepare() {
                UserCommand.Register command = userFactory.of(UserSampleFactory.createRequestParam());
                User savedUser = userService.registerUser(command);
                givenRequest.setEmail(command.getEmail());
                givenRequest.setPassword("wrong_password");
            }

            @Test
            @DisplayName("BAD_REQUEST(400)와 에러 메세지를 리턴한다")
            void it_returns_201_registered_user() throws Exception {
                final ResultActions resultActions = subject(givenRequest);

                resultActions.andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("failed")))
                        .andDo(print());
            }
        }
    }
}
