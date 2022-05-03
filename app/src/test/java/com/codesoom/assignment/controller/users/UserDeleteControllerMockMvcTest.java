package com.codesoom.assignment.controller.users;

import com.codesoom.assignment.TestUtil;
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
import org.springframework.test.web.servlet.MockMvc;

import static com.codesoom.assignment.ConstantsForTest.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserDeleteController 클래스")
public class UserDeleteControllerMockMvcTest extends ControllerTest {

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
                User user = repository.save(new User("김철수", "kimcs@codesoom.com", "rlacjftn098"));
                this.USER_ID = user.getId();
                this.TOKEN = TestUtil.generateToken(mockMvc, user.getEmail(), user.getPassword());
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
                User user = repository.save(new User("김철수", "kimcs@codesoom.com", "rlacjftn098"));
                this.NOT_USER_ID = user.getId() + 100;
                this.TOKEN = TestUtil.generateToken(mockMvc, user.getEmail(), user.getPassword());
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
