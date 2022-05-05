package com.codesoom.assignment.controllers.user;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.common.message.NormalMessage;
import com.codesoom.assignment.config.AutoConfigureMockMvcExtendForUtf8;
import com.codesoom.assignment.domain.crypt.CryptService;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserService;
import com.codesoom.assignment.dto.user.UserRegistrationData;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("회원가입시 요청시")
@AutoConfigureMockMvcExtendForUtf8
class RegistrationControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final CryptService cryptService;


    private String userEmail = "dev.bslee@gmail";
    private String name = "이병성";
    private String password = "0104555";


    @Autowired
    public RegistrationControllerTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserService userService,
            CryptService cryptService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.cryptService = cryptService;
    }

    @Nested
    @DisplayName("필수 값 비밀번호는")
    class Describe_Require_Field_Password {
        private UserRegistrationData userRegistrationData;

        @Nested
        @DisplayName("적합하지 않은 값이 주어진다면[공백]")
        class Context_empty {
            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(userEmail, name, "");
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 공백이 허용되지 않습니다]를 응답한다")
            void will_status_badRequest_비밀번호_공백이_허용되지_않습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
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
                userRegistrationData = new UserRegistrationData(userEmail, name, "2");
            }
            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 {설정된 자리수} 이상 이여야합니다}]를 응답한다")
            void will_status_badRequest_비밀번호는_설정된_자리수_이상_이여야합니다() throws Exception{
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
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
                userRegistrationData = new UserRegistrationData(userEmail, name, "22222222222222222222222222222222222222222222222222222222222222222222");
            }
            @Test
            @DisplayName("상태코드[400]과 메세지:[비밀번호는 {설정된 자리수} 이하 이여야합니다}]를 응답한다")
            void will_status_badRequest_비밀번호는_설정된_자리수_이상_이여야합니다() throws Exception{
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                AssertionsForClassTypes.assertThat(responseBody).contains(ErrorMessage.PASSWORD_LENGTH_BELOW_SUFFIX.getErrorMsg());
            }
        }


        @Nested
        @DisplayName("적합한 값이 주어진다면")
        class Context_fitness {
            private String fitnessEmail = "hello?";

            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(fitnessEmail, name, password);
            }

            @Test
            @DisplayName("상태코드[201]과 메세지:[회원가입이 성공했습니다!]를 응답한다")
            void will_status_isOk_and_회원가입이_성공했습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isCreated()).andReturn();


                String responseBody = mvcResult.getResponse().getContentAsString();
                User user = userService.findUserByEmail(fitnessEmail);

                assertThat(responseBody).contains(NormalMessage.JOIN_OK.getNormalMsg());
                assertThat(user.getEmail()).isEqualTo(fitnessEmail);
                assertThat(user.getName()).isEqualTo(name);
                assertThat(user.isMatchPassword(cryptService, password)).isTrue();

            }
        }

    }

    @Nested
    @DisplayName("필수 값 이름은")
    class Describe_Require_Field_Name {
        private UserRegistrationData userRegistrationData;

        @Nested
        @DisplayName("적합하지 않은 값이 주어진다면[공백]")
        class Context_empty {

            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(userEmail, "", password);
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[이름은 공백이 허용되지 않습니다]를 응답한다")
            void will_status_badRequest_이름_공백이_허용되지_않습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                assertThat(responseBody).contains(ErrorMessage.NAME_IS_EMPTY.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합한 값이 주어진다면")
        class Context_fitness {


            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(userEmail, name, password);
            }

            @Test
            @DisplayName("상태코드[201]과 메세지:[회원가입이 성공했습니다!]를 응답한다")
            void will_status_isOk_and_회원가입이_성공했습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isCreated()).andReturn();


                String responseBody = mvcResult.getResponse().getContentAsString();
                User user = userService.findUserByEmail(userEmail);

                assertThat(responseBody).contains(NormalMessage.JOIN_OK.getNormalMsg());
                assertThat(user.getEmail()).isEqualTo(userEmail);
                assertThat(user.getName()).isEqualTo(name);
                assertThat(user.isMatchPassword(cryptService, password)).isTrue();
            }
        }

    }

    @Nested
    @DisplayName("필수 값 이메일은")
    class Describe_Require_Field_Email {

        private UserRegistrationData userRegistrationData;

        @Nested
        @DisplayName("적합하지 않은 값이 주어진다면[공백]")
        class Context_empty {

            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData("", "이병성", "0104555");
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[이메일은 공백이 허용되지 않습니다]를 응답한다")
            void will_status_badRequest_이메일은_공백이_허용되지_않습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                assertThat(responseBody).contains(ErrorMessage.EMAIL_IS_EMPTY.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합하지 않은 값이 주어진다면[존재하는 이메일]")
        class Context_duplicate_email {

            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(userEmail, name, password);
                userService.join(userRegistrationData);
            }

            @Test
            @DisplayName("상태코드[400]과 메세지:[존재하는 이메일 입니다]를 응답한다.")
            void will_status_badRequest_존재하는_이메일_입니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isBadRequest()).andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                assertThat(responseBody).contains(ErrorMessage.EMAIL_IS_DUPLICATE.getErrorMsg());
            }


        }

        @Nested
        @DisplayName("적합한 값이 주어진다면")
        class Context_fitness {
            private String fitnessEmail = "hello?";

            @BeforeEach
            public void setUp() {
                userRegistrationData = new UserRegistrationData(fitnessEmail, name, password);
            }

            @Test
            @DisplayName("상태코드[201]과 메세지:[회원가입이 성공했습니다!]를 응답한다")
            void will_status_isOk_and_회원가입이_성공했습니다() throws Exception {
                MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(userRegistrationData))
                ).andExpect(status().isCreated()).andReturn();


                String responseBody = mvcResult.getResponse().getContentAsString();
                User user = userService.findUserByEmail(fitnessEmail);

                assertThat(responseBody).contains(NormalMessage.JOIN_OK.getNormalMsg());
                assertThat(user.getEmail()).isEqualTo(fitnessEmail);
                assertThat(user.getName()).isEqualTo(name);
                assertThat(user.isMatchPassword(cryptService, password)).isTrue();

            }
        }
    }

}