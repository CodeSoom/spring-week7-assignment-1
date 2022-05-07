package com.codesoom.assignment.controllers.auth;


import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.common.message.NormalMessage;
import com.codesoom.assignment.config.AutoConfigureMockMvcExtendForUtf8;
import com.codesoom.assignment.domain.user.UserModifier;
import com.codesoom.assignment.dto.auth.AuthRequestData;
import com.codesoom.assignment.security.jwt.JwtParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("로그인 요청시")
@AutoConfigureMockMvcExtendForUtf8
class LoginRequestTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserModifier userModifier;
    private final JwtParser jwtParser;

    private String userEmail = "dev.bslee@gmail";
    private String password = "0104555";


    @Autowired
    public LoginRequestTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserModifier userModifier,
            JwtParser jwtParser
    ) {
        this.mockMvc = mockMvc;
        this.jwtParser = jwtParser;
        this.objectMapper = objectMapper;
        this.userModifier = userModifier;
    }

    @Nested
    @DisplayName("필수 값 비밀번호는")
    class Describe_Require_Field_Password {

        private AuthRequestData authRequestData;

        @Nested
        @DisplayName("적합하지 않은 값이 주어진다면[공백]")
        class Context_empty {
            @BeforeEach
            public void setUp() {
                authRequestData = AuthRequestData.builder()
                        .email(userEmail)
                        .password("")
                        .build();

            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 공백이 허용되지 않습니다]를 응답한다")
            void will_status_badRequest_비밀번호_공백이_허용되지_않습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/session")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(authRequestData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                assertThat(responseBody).contains(ErrorMessage.PASSWORD_IS_EMPTY.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합하지 않은 값[설정된 자리수 이하라면]")
        class Context_length_below {
            @BeforeEach
            public void setUp() {
                authRequestData = AuthRequestData.builder()
                        .email(userEmail)
                        .password("2")
                        .build();
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 {설정된 자리수} 이상 이여야합니다}]를 응답한다")
            void will_status_badRequest_비밀번호는_설정된_자리수_이상_이여야합니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/session")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(authRequestData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                AssertionsForClassTypes.assertThat(responseBody).contains(ErrorMessage.PASSWORD_LENGTH_MORE_THAN_SUFFIX.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합하지 않은 값[설정된 자리수 이상이라면]")
        class Context_length_more_than {
            @BeforeEach
            public void setUp() {
                authRequestData = AuthRequestData.builder()
                        .email(userEmail)
                        .password("22222222222222222222222222222222222222222222222222222222222222222222")
                        .build();
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 {설정된 자리수} 이하 이여야합니다}]를 응답한다")
            void will_status_badRequest_비밀번호는_설정된_자리수_이하_이여합니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/session")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(authRequestData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                AssertionsForClassTypes.assertThat(responseBody).contains(ErrorMessage.PASSWORD_LENGTH_BELOW_SUFFIX.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합하지 않은 값[회원 가입시 입력한 비밀번호]")
        class Context_password_not_match {
            @BeforeEach
            public void setUp() {
                authRequestData = AuthRequestData.builder()
                        .email(userEmail)
                        .password(password + "2222")
                        .build();

                AuthRequestData joinInfo = AuthRequestData.builder()
                        .email(userEmail)
                        .password(password)
                        .build();

                userModifier.join(joinInfo.toEntity());
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호를 확인해주세요]를 응답한다")
            void will_status_badRequest_비밀번호를_확인해주세요() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/session")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(authRequestData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();

                AssertionsForClassTypes.assertThat(responseBody).contains(ErrorMessage.PASSWORD_NOT_MATCH.getErrorMsg());
            }
        }


        @Nested
        @DisplayName("적합한 값이 주어진다면")
        class Context_fitness {

            @BeforeEach
            public void setUp() {
                authRequestData = AuthRequestData.builder()
                        .email(userEmail)
                        .password(password)
                        .build();
                userModifier.join(authRequestData.toEntity());
            }

            @Test
            @DisplayName("상태코드[201] 메세지:[{email}님 환영합니다] jwt토큰 를 응답한다")
            void will_status_isOk_and_회원가입이_성공했습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/session")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(authRequestData))
                ).andExpect(status().isCreated()).andReturn();


                String responseBody = mvcResult.getResponse().getContentAsString();
                assertThat(responseBody).contains(NormalMessage.LOGIN_OK.getNormalMsg());

                Map<String, Object> parseResponse = objectMapper.readValue(responseBody, HashMap.class);
                System.out.println(parseResponse.get("data"));
            }
        }

    }

}