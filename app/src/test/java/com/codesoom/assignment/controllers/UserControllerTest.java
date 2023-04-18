package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

  private static final String MY_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
      "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
  private static final String OTHER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjJ9."
      + "TEM6MULsZeqkBbUKziCR4Dg_8kymmZkyxsCXlfNJ3g0";
  private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEwMDR9.3GV5ZH3flBf0cnaXQCNNZlT4mgyFyBUhn3LKzQohh1A";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() throws AccessDeniedException {
    given(userService.registerUser(any(UserRegistrationData.class)))
        .will(invocation -> {
          UserRegistrationData registrationData = invocation.getArgument(0);
          return User.builder()
              .id(13L)
              .email(registrationData.getEmail())
              .name(registrationData.getName())
              .build();
        });

    given(userService.updateUser(eq(1L), any(UserModificationData.class), eq(1L)))
        .will(invocation -> {
          Long id = invocation.getArgument(0);
          UserModificationData modificationData =
              invocation.getArgument(1);
          return User.builder()
              .id(id)
              .email("tester@example.com")
              .name(modificationData.getName())
              .build();
        });

    given(userService.updateUser(eq(100L), any(UserModificationData.class), eq(1L)))
        .willThrow(new UserNotFoundException(100L));

//    given(userService.updateUser(eq(1L), any(UserModificationData.class), eq(2L)))
//        .willThrow(new AccessDeniedException("권한이 없습니다."));

    given(userService.deleteUser(100L))
        .willThrow(new UserNotFoundException(100L));

    //todo: token given
    given(authenticationService.parseToken(MY_TOKEN)).willReturn(1L);
    given((authenticationService).parseToken(OTHER_TOKEN)).willReturn(2L);
    given((authenticationService).parseToken(ADMIN_TOKEN)).willReturn(1004L);

    given(authenticationService.roles(eq(1L))).willReturn(List.of(new Role("USER")));
    given(authenticationService.roles(eq(2L))).willReturn(List.of(new Role("USER")));
    given(authenticationService.roles(eq(1004L))).willReturn(List.of(new Role("ADMIN")));
  }

  @Test
  void registerUserWithValidAttributes() throws Exception {
    mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"tester@example.com\"," +
                    "\"name\":\"Tester\",\"password\":\"test\"}")
        )
        .andExpect(status().isCreated())
        .andExpect(content().string(
            containsString("\"id\":13")
        ))
        .andExpect(content().string(
            containsString("\"email\":\"tester@example.com\"")
        ))
        .andExpect(content().string(
            containsString("\"name\":\"Tester\"")
        ));

    verify(userService).registerUser(any(UserRegistrationData.class));
  }

  @Test
  void registerUserWithInvalidAttributes() throws Exception {
    mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUserWithValidAttributes() throws Exception {
    mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TEST\",\"password\":\"test\"}")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(
            containsString("\"id\":1")
        ))
        .andExpect(content().string(
            containsString("\"name\":\"TEST\"")
        ));

    verify(userService).updateUser(eq(1L), any(UserModificationData.class), eq(1L));
  }


  @Test
  void updateUserWithInvalidAttributes() throws Exception {
    mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"password\":\"\"}")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUserWithNotExsitedId() throws Exception {
    mockMvc.perform(
            patch("/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                .header("Authorization", "Bearer " + MY_TOKEN)
        )
        .andExpect(status().isNotFound());

    verify(userService)
        .updateUser(eq(100L), any(UserModificationData.class), eq(1L));
  }

  @DisplayName("토큰이 없는 경우")
  @Test
  void updateUserWithOutAccessToken() throws Exception {
    mockMvc.perform(
            patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
        )
        .andExpect(status().isUnauthorized());
  }

//  @DisplayName("다른 토큰을 이용하는 경우")
//  @Test
//  void updateUserWithOtherAccessToken() throws Exception {
//    mockMvc.perform(
//        patch("/users/1")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
//            .header("Authorization", "Bearer " + OTHER_TOKEN)
//    );
////        .andExpect(status().isForbidden());
//    verify(userService)
//        .updateUser(eq(100L), any(UserModificationData.class), eq(100L));
//  }


  @Test
  void destroyWithExistedId() throws Exception {
    mockMvc.perform(delete("/users/1")
            .header("Authorization", "Bearer " + ADMIN_TOKEN)
        )
        .andExpect(status().isNoContent());

    verify(userService).deleteUser(1L);
  }

  @Test
  void destroyWithOutToken() throws Exception {
    mockMvc.perform(delete("/users/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void destroyWithOutAdminRole() throws Exception {
    mockMvc.perform(delete("/users/1")
            .header("Authorization", "Bearer " + MY_TOKEN)
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void destroyWithNotExistedId() throws Exception {
    mockMvc.perform(delete("/users/100")
            .header("Authorization", "Bearer " + ADMIN_TOKEN)
        )
        .andExpect(status().isNotFound());

    verify(userService).deleteUser(100L);
  }
}
