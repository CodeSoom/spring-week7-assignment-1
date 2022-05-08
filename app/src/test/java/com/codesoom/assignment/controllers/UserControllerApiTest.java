package com.codesoom.assignment.controllers;

import com.codesoom.assignment.annotations.Utf8MockMvc;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Utf8MockMvc
@SpringBootTest
@DisplayName("/users")
public class UserControllerApiTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtUtil jwtUtil;

    @Nested
    @DisplayName("/ 경로는 ")
    class Describe_root_path {
        @Nested
        @DisplayName("PATCH /users/{id} 요청을 받았을 때")
        class Context_patch_request_with_user_id_as_path_variable {
            @Nested
            @DisplayName("{id} 를 가진 사용자의 유효한 인증 토큰과 사용자 업데이트 정보를 받는다면")
            class Context_valid_token_and_user_update_data {
                private final MockHttpServletRequestBuilder requestBuilder;
                private final User user = setUpValidUser();
                private final String validToken = jwtUtil.encode(user.getId());
                private final ResultActions resultActions;

                public Context_valid_token_and_user_update_data() throws Exception {
                    requestBuilder = patch("/users/" + user.getId());

                    String json = "{\"name\":\"" + user.getName() + "updated" + "\"}";

                    requestBuilder
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + validToken);

                    resultActions = mockMvc.perform(requestBuilder);
                }

                @Test
                @DisplayName("200 OK 응답을 전달한다.")
                void it_sends_200_ok_response() throws Exception {
                    resultActions.andExpect(status().isOk());
                }

                @Test
                @DisplayName("사용자를 업데이트 한다.")
                void it_updates_user() {
                    User updatedUser = userService.findUser(this.user.getId());
                    assertThat(updatedUser.getId()).isEqualTo(user.getId());
                    assertThat(updatedUser.getName()).isEqualTo(user.getName() + "updated");
                    assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
                }
            }

            @Nested
            @DisplayName("유효하지 않은 인증 토큰을 받는다면")
            class Context_invalid_token {
                private final MockHttpServletRequestBuilder requestBuilder;
                private final User user = setUpValidUser();
                private final String validToken = jwtUtil.encode(user.getId());
                private final String invalidToken = validToken + "X";
                private final ResultActions resultActions;

                public Context_invalid_token() throws Exception {
                    requestBuilder = patch("/users/" + user.getId());

                    requestBuilder
                            .header("Authorization", "Bearer " + invalidToken);

                    resultActions = mockMvc.perform(requestBuilder);
                }

                @Test
                @DisplayName("401 Unauthorized 응답을 전달한다.")
                void it_sends_401_unauthorized_response() throws Exception {
                    resultActions.andExpect(status().isUnauthorized());
                }
            }

            @Nested
            @DisplayName("다른 {id} 를 가진 사용자의 토큰을 받는다면")
            class Context_invalid_token_and_user_update_data {
                private final MockHttpServletRequestBuilder requestBuilder;
                private final User user = setUpValidUser();
                private final String validButDifferentIdToken = jwtUtil.encode(user.getId() + 10L);
                private final ResultActions resultActions;

                public Context_invalid_token_and_user_update_data() throws Exception {
                    requestBuilder = patch("/users/" + user.getId());

                    String json = "{\"name\":\"" + user.getName() + "updated" + "\"}";

                    requestBuilder
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + validButDifferentIdToken);

                    resultActions = mockMvc.perform(requestBuilder);
                }

                @Test
                @DisplayName("403 Forbidden 응답을 전달한다.")
                void it_sends_403_forbidden_response() throws Exception {
                    resultActions
                            .andExpect(status().isForbidden())
                            .andDo(print());
                }
            }
        }
    }

    public User setUpValidUser() {
        initUserRepository();
        return userService.registerUser(
                UserRegistrationData.builder()
                        .email("test@example.com")
                        .name("김김김")
                        .password("password1234")
                        .build()
        );
    }

    public void initUserRepository() {
        userRepository.deleteAll();
    }
}
