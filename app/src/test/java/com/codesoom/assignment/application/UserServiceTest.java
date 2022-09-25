package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("update()")
    class Describe_Update{

        @Nested
        @DisplayName("식별자에 해당하는 사용자가 존재한다면")
        class Context_ExistedUser{
            private final UserRegistrationData registrationData = new UserRegistrationData("register@google.com", "register", "register");
            private final Long registeredId = userService.registerUser(registrationData).getId();

            @AfterEach
            void tearDown() {
                userService.deleteUser(registeredId);
            }

            @Nested
            @DisplayName("수정자와 등록자가 서로 다르다면")
            class Context_ModifierNotEqualsRegister{

                private final Long modifierId = registeredId + 1L;
                private final UserModificationData modificationData = new UserModificationData("modifier" , "modifier");

                @Test
                @DisplayName("접근을 거부한다는 상태를 반환하고 예외를 던진다.")
                void It_ThrowException() {
                    assertThatThrownBy(() -> userService.updateUser(registeredId , modificationData , modifierId))
                            .isInstanceOf(AccessDeniedException.class);
                }
            }
        }
    }
}
