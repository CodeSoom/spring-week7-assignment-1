package com.codesoom.assignment.controller.users;

import com.codesoom.assignment.TokenGenerator;
import com.codesoom.assignment.controller.ControllerTest;
import com.codesoom.assignment.domain.users.User;
import com.codesoom.assignment.domain.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.codesoom.assignment.ConstantsForTest.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserDeleteController 클래스")
public class UserDeleteControllerMockMvcTest extends ControllerTest {

    private final String EMAIL = "kimcs@codesoom.com";
    private final String PASSWORD = "rlacjftn098";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        cleanup();
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    private User saveUser() {
        User user = User.of("김철수", EMAIL);
        user.changePassword(PASSWORD, passwordEncoder);

        return repository.save(user);
    }

    @DisplayName("[DELETE] /users/{id}")
    @Nested
    class Describe_delete_user {

        @DisplayName("본인의 id가 주어지면")
        @Nested
        class Context_with_exist_user {

            private Long USER_ID;
            private String TOKEN;

            @BeforeEach
            void setup() throws Exception {
                User user = saveUser();
                this.USER_ID = user.getId();
                this.TOKEN = TokenGenerator.generateToken(mockMvc, EMAIL, PASSWORD);
            }

            @DisplayName("성공적으로 회원 정보를 삭제한다.")
            @Test
            void it_will_delete_user() throws Exception {
                mockMvc.perform(delete("/users/" + USER_ID)
                                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + TOKEN))
                        .andExpect(status().isNoContent());

                assertThat(repository.findById(USER_ID)).isEmpty();
            }
        }

        @DisplayName("본인이 아닌 id가 주어지면")
        @Nested
        class Context_with_not_authorize {
            private Long NOT_USER_ID;
            private String TOKEN;

            @BeforeEach
            void setup() throws Exception {
                User user = saveUser();
                this.NOT_USER_ID = user.getId() + 100;
                this.TOKEN = TokenGenerator.generateToken(mockMvc, EMAIL, PASSWORD);
            }

            @DisplayName("403 forbidden을 응답한다.")
            @Test
            void it_response_403_forbidden() throws Exception {
                mockMvc.perform(delete("/users/" + NOT_USER_ID)
                                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + TOKEN))
                        .andExpect(status().isForbidden());
            }
        }
    }

}
