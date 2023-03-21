package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.security.UserAuthentication;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private static final Long DELETED_USER_ID = 200L;

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final AuthenticationService authenticationService = mock(AuthenticationService.class);

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(mapper, userRepository, passwordEncoder, authenticationService);

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

        given(userRepository.findByIdAndDeletedIsFalse(1L))
                .willReturn(Optional.of(
                        User.builder()
                                .id(1L)
                                .email(EXISTED_EMAIL_ADDRESS)
                                .name("Tester")
                                .password(passwordEncoder.encode("test"))
                                .build()));

        given(userRepository.findByIdAndDeletedIsFalse(100L))
                .willReturn(Optional.empty());

        given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                .willReturn(Optional.empty());

        // 들어온 유저의 ID는 1L로 가정하는 구문
        given(authenticationService.isPrincipal(1L)).willReturn(true);
        given(authenticationService.isPrincipal(2L)).willReturn(false);
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
                .name("TEST")
                .password("TEST")
                .build();

        User user = userService.updateUser(1L, modificationData);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);
        assertThat(user.getName()).isEqualTo("TEST");

        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void updateUserWithNotExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("TEST")
                .build();

        assertThatThrownBy(() -> userService.updateUser(100L, modificationData))
                .isInstanceOf(AccessDeniedException.class);

    }


    @Test
    void updateUserWithDeletedId() {

        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("TEST")
                .build();

        assertThatThrownBy(
                () -> userService.updateUser(DELETED_USER_ID, modificationData)
        )
                .isInstanceOf(AccessDeniedException.class);

    }

    @Test
    void updateUserByPrincipal() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("TEST")
                .build();

        User user = userService.updateUser(1L, modificationData);

        assertThat(user.getName().equals("TEST"));
        assertThat(passwordEncoder.matches("TEST", user.getPassword())).isTrue();
        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void updateUserByOthers() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("TEST")
                .build();

        assertThatThrownBy(
                () -> userService.updateUser(2L, modificationData)
        )
                .isInstanceOf(AccessDeniedException.class);

    }

    @Test
    void deleteUserWithExistedId() {
        User user = userService.deleteUser(1L);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.isDeleted()).isTrue();

        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void deleteUserWithNotExistedId() {
        assertThatThrownBy(() -> userService.deleteUser(100L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(100L);
    }

    @Test
    void deleteUserWithDeletedId() {
        assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }
}
