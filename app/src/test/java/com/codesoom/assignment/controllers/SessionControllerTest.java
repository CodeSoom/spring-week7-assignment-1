package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    private String refreshValidToken;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private JwtUtil jwtUtil = new JwtUtil("12345678901234567890123456789010");
    @BeforeEach
    void setUp() {
        SessionResponseData sessionResponseData = SessionResponseData.builder()
                        .accessToken("a.b.c")
                        .refreshToken("d.e.f")
                        .build();

        given(authenticationService.login("tester@example.com", "test"))
                .willReturn(sessionResponseData);

        given(authenticationService.login("badguy@example.com", "test"))
                .willThrow(new LoginFailException("badguy@example.com"));

        given(authenticationService.login("tester@example.com", "xxx"))
                .willThrow(new LoginFailException("tester@example.com"));

        given(authenticationService.reissueAccessToken(refreshValidToken))
                .willReturn(sessionResponseData);

        User user = User.builder()
                .id(1L)
                .role("ROLE_USER")
                .build();

        refreshValidToken = jwtUtil.createRefreshToken(user);
        given(authenticationService.reissueAccessToken(refreshValidToken)).willReturn(sessionResponseData);
    }

    @Test
    void loginWithRightEmailAndPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"tester@example.com\"," +
                                "\"password\":\"test\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(".")));
    }

    @Test
    void loginWithWrongEmail() throws Exception {
        mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"badguy@example.com\"," +
                                "\"password\":\"test\"}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"tester@example.com\"," +
                                "\"password\":\"xxx\"}")
        )
                .andExpect(status().isBadRequest());
    }


    @Test
    void accessTokenReissueByRefreshToken() throws Exception {
        mockMvc.perform(
                        post("/session/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("RefreshToken", "Bearer " + refreshValidToken)
                )
                .andExpect(content().string(containsString(".")));
    }
}
