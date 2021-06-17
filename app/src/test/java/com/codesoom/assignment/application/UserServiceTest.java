package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Nested
class UserServiceTest {
    private static final String EXISTED_EMAIL_ADDRESS = "existed@example.com";
    private static final Long DELETED_USER_ID = 200L;

    private static final String NAME_BEFORE_UPDATE = "Tester";
    private static final String NAME_AFTER_UPDATE = "UPDATE";

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(mapper, userRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("회원가입시")
    class RegisterUser {

        @Nested
        @DisplayName("사용할 수 있는 이메일이면")
        class WithAvailableEmail {

            @BeforeEach
            void setUp() {
                given(userRepository.save(any(User.class))).will(invocation -> {
                    User source = invocation.getArgument(0);
                    return User.builder()
                            .id(13L)
                            .email(source.getEmail())
                            .name(source.getName())
                            .build();
                });
            }

            @Test
            @DisplayName("사용자를 등록한다.")
            void registerUser() {
                UserRegistrationData registrationData = UserRegistrationData.builder()
                        .email("tester@example.com")
                        .name(NAME_BEFORE_UPDATE)
                        .password("test")
                        .build();

                User user = userService.registerUser(registrationData);

                assertThat(user.getId()).isEqualTo(13L);
                assertThat(user.getEmail()).isEqualTo("tester@example.com");
                assertThat(user.getName()).isEqualTo(NAME_BEFORE_UPDATE);
                assertThat(user.getPassword()).isEqualTo("");

                verify(userRepository).save(any(User.class));
            }

        }

        @Nested
        @DisplayName("이미 존재하는 이메일이면")
        class WithExistedEmail {

            @BeforeEach
            void setUp() {
                given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                        .willReturn(true);
            }

            @Test
            @DisplayName("이메일 중복 예외를 던진다.")
            void registerUserWithDuplicatedEmail() {
                UserRegistrationData registrationData = UserRegistrationData.builder()
                        .email(EXISTED_EMAIL_ADDRESS)
                        .name(NAME_BEFORE_UPDATE)
                        .password("test")
                        .build();

                assertThatThrownBy(() -> userService.registerUser(registrationData))
                        .isInstanceOf(UserEmailDuplicationException.class);

                verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
            }
        }
    }

    @Nested
    @DisplayName("회원 정보를 수정할 때")
    class UpdateUserInfo {

        @Nested
        @DisplayName("수정하려는 회원이 존재한다면")
        class WithExistedUser {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(1L))
                        .willReturn(Optional.of(
                                User.builder()
                                        .id(1L)
                                        .email(EXISTED_EMAIL_ADDRESS)
                                        .name(NAME_BEFORE_UPDATE)
                                        .password("test")
                                        .build()));
            }

            @Test
            @DisplayName("회원의 정보를 수정한다.")
            void updateUserWithExistedId() {

                UserModificationData modificationData = UserModificationData.builder()
                        .name(NAME_AFTER_UPDATE)
                        .password("TEST")
                        .build();

                User user = userService.updateUser(1L, modificationData);

                assertThat(user.getId()).isEqualTo(1L);
                assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);
                assertThat(user.getName()).isEqualTo(NAME_AFTER_UPDATE);

                verify(userRepository).findByIdAndDeletedIsFalse(1L);
            }
        }

        @Nested
        @DisplayName("해당 회원이 존재하지 않으면")
        class WithNotExistedUser {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(100L))
                        .willReturn(Optional.empty());

                given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("예외를 던진다.")
            void updateUserWithNotExistedId() {
                UserModificationData modificationData = UserModificationData.builder()
                        .name(NAME_AFTER_UPDATE)
                        .password("TEST")
                        .build();

                assertThatThrownBy(() -> userService.updateUser(100L, modificationData))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(100L);
            }

            @Test
            @DisplayName("이미 삭제된 회원이기 때문에 예외를 던진다.")
            void updateUserWithDeletedId() {
                UserModificationData modificationData = UserModificationData.builder()
                        .name(NAME_AFTER_UPDATE)
                        .password("TEST")
                        .build();

                assertThatThrownBy(
                        () -> userService.updateUser(DELETED_USER_ID, modificationData)
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }

    @Nested
    @DisplayName("회원을 삭제할 때")
    class DeleteUser {

        @Nested
        @DisplayName("존재하는 회원을 삭제한다면")
        class WithExistedUser {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(1L))
                        .willReturn(Optional.of(
                                User.builder()
                                        .id(1L)
                                        .email(EXISTED_EMAIL_ADDRESS)
                                        .name(NAME_BEFORE_UPDATE)
                                        .password("test")
                                        .build()));
            }

            @Test
            @DisplayName("정상적으로 삭제할 수 있다.")
            void deleteUserWithExistedId() {
                User user = userService.deleteUser(1L);

                assertThat(user.getId()).isEqualTo(1L);
                assertThat(user.isDeleted()).isTrue();

                verify(userRepository).findByIdAndDeletedIsFalse(1L);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 회원을 삭제한다면")
        class WithNotExistedUser {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(100L))
                        .willReturn(Optional.empty());

                given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("예외를 던진다.")
            void deleteUserWithNotExistedId() {
                assertThatThrownBy(() -> userService.deleteUser(100L))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(100L);
            }

            @Test
            @DisplayName("이미 삭제된 회원이기 때문에 예외를 던진다.")
            void deleteUserWithDeletedId() {
                assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }
}
