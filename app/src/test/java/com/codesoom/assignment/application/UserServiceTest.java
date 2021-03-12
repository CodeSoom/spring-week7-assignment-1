package com.codesoom.assignment.application;

import com.codesoom.assignment.config.AppConfig;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private static final String EXISTED_EMAIL_ADDRESS = "existed@example.com";
    private static final String USER2_EMAIL_ADDRESS = "test2@test.com";
    private static final Long NOT_EXIST_USER_ID = 100L;
    private static final Long DELETED_USER_ID = 200L;
    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;
    private static final String USER1_NAME = "Tester";
    private static final String USER2_NAME = "Tester2";
    private static final String USER1_PASSWORD = "test";
    private static final String USER2_PASSWORD = "test2";
    private static final String MODIFIED_NAME = "TEST";
    private static final String MODIFIED_PASSWORD = "TEST";

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private Authentication authentication = mock(Authentication.class);

    private SecurityContext securityContext = mock(SecurityContext.class);

    @Autowired
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
        PasswordEncoder passwordEncoder = appConfig.passwordEncoder();

        userService = new UserService(userRepository, passwordEncoder);

        given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                .willReturn(true);

        given(userRepository.save(any(User.class))).will(invocation -> {
            User source = invocation.getArgument(0);
            return User.builder()
                    .id(13L)
                    .email(source.getEmail())
                    .name(source.getName())
                    .build();
        });

        given(userRepository.findByIdAndDeletedIsFalse(USER1_ID))
                .willReturn(Optional.of(
                        User.builder()
                                .id(USER1_ID)
                                .email(EXISTED_EMAIL_ADDRESS)
                                .name(USER1_NAME)
                                .password(USER1_PASSWORD)
                                .build()));

        given(userRepository.findByIdAndDeletedIsFalse(USER2_ID))
                .willReturn(Optional.of(
                        User.builder()
                                .id(USER2_ID)
                                .email(USER2_EMAIL_ADDRESS)
                                .name(USER2_NAME)
                                .password(USER2_PASSWORD)
                                .build()));

        given(userRepository.findByIdAndDeletedIsFalse(NOT_EXIST_USER_ID))
                .willReturn(Optional.empty());

        given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                .willReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        given(authentication.getPrincipal()).willReturn(USER1_ID);
    }

    @Test
    void registerUser() {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .email("tester@example.com")
                .name("Tester")
                .password("test")
                .build();

        User user = userService.registerUser(registrationData);

        assertThat(user.getId()).isEqualTo(13L);
        assertThat(user.getEmail()).isEqualTo("tester@example.com");
        assertThat(user.getName()).isEqualTo("Tester");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserWithDuplicatedEmail() {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .email(EXISTED_EMAIL_ADDRESS)
                .name("Tester")
                .password("test")
                .build();

        assertThatThrownBy(() -> userService.registerUser(registrationData))
                .isInstanceOf(UserEmailDuplicationException.class);

        verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
    }

    @Test
    void updateUserWithExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name(MODIFIED_NAME)
                .password(MODIFIED_PASSWORD)
                .build();

        User user = userService.updateUser(USER1_ID, modificationData);

        assertThat(user.getId()).isEqualTo(USER1_ID);
        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);
        assertThat(user.getName()).isEqualTo(MODIFIED_NAME);

        verify(userRepository).findByIdAndDeletedIsFalse(USER1_ID);
    }

    @Test
    void updateUserWithNotExistedId() {
        given(authentication.getPrincipal())
                .willReturn(NOT_EXIST_USER_ID);

        UserModificationData modificationData = UserModificationData.builder()
                .name(MODIFIED_NAME)
                .password(MODIFIED_PASSWORD)
                .build();

        assertThatThrownBy(() -> userService.updateUser(NOT_EXIST_USER_ID, modificationData))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXIST_USER_ID);
    }


    @Test
    void updateUserWithDeletedId() {
        given(authentication.getPrincipal()).willReturn(DELETED_USER_ID);

        UserModificationData modificationData = UserModificationData.builder()
                .name(MODIFIED_NAME)
                .password(MODIFIED_PASSWORD)
                .build();

        assertThatThrownBy(
                () -> userService.updateUser(DELETED_USER_ID, modificationData)
        )
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }

    @Test
    void deleteUserWithExistedId() {
        User user = userService.deleteUser(USER1_ID);

        assertThat(user.getId()).isEqualTo(USER1_ID);
        assertThat(user.isDeleted()).isTrue();

        verify(userRepository).findByIdAndDeletedIsFalse(USER1_ID);
    }

    @Test
    void deleteUserWithNotExistedId() {
        assertThatThrownBy(() -> userService.deleteUser(NOT_EXIST_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXIST_USER_ID);
    }

    @Test
    void deleteUserWithDeletedId() {
        assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }
}
