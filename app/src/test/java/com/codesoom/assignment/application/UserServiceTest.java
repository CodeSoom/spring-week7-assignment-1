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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private static final String USER_NAME = "name";
    private static final String USER_EMAIL = "email@email.com";
    private static final String USER_RAW_PASSWORD = "password";
    private static final String EXISTED_EMAIL_ADDRESS = "existed@example.com";
    private static final String UPDATE_PREFIX = "update";
    private static final Long EXISTED_USER_ID = 1L;
    private static final Long NOT_EXISTED_USER_ID = 100L;
    private static final Long DELETED_USER_ID = 200L;

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private UserRegistrationData registrationData;

    private UserModificationData userModificationData;

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();

        userService = new UserService(mapper, userRepository, passwordEncoder);

        registrationData = UserRegistrationData.builder()
                .name(USER_NAME)
                .email(USER_EMAIL)
                .password(USER_RAW_PASSWORD)
                .build();

        userModificationData = UserModificationData.builder()
                .name(UPDATE_PREFIX + USER_NAME)
                .password(USER_RAW_PASSWORD)
                .build();
    }



    @Nested
    @DisplayName("registerUser 메소드는")
    class Describe_registerUser_method {

        @Nested
        @DisplayName("유효한 사용자 등록 정보가 주어지면")
        class Context_with_valid_userRegistrationData {

            @BeforeEach
            void prepareUserRegistrationData() {
                given(userRepository.save(any(User.class))).will(invocation -> {
                    User source = invocation.getArgument(0);
                    return User.builder()
                            .email(source.getEmail())
                            .name(source.getName())
                            .build();
                });
            }

            @Test
            @DisplayName("새 사용자를 등록하고 리턴한다")
            void it_returns_new_user() {
                User user = userService.registerUser(registrationData);

                assertThat(user.getName()).isEqualTo(registrationData.getName());
                assertThat(user.getEmail()).isEqualTo(registrationData.getEmail());

                String encodedPassword = user.getPassword();
                assertThat(encodedPassword).isNotEqualTo(registrationData.getPassword());

                verify(userRepository).save(any(User.class));
            }
        }

        @Nested
        @DisplayName("만약 존재하는 Email이 주어지면")
        class Describe_with_existed_email {

            @BeforeEach
            void prepareUserRegistrationData() {
                registrationData = UserRegistrationData.builder()
                        .name(USER_NAME)
                        .email(EXISTED_EMAIL_ADDRESS)
                        .password(USER_RAW_PASSWORD)
                        .build();

                given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS)).willReturn(true);
            }

            @Test
            @DisplayName("사용자 이메일 중복 에러를 던진다")
            void it_returns_userEmailDuplicationException() {
                assertThatThrownBy(() -> userService.registerUser(registrationData))
                        .isInstanceOf(UserEmailDuplicationException.class);

                verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
            }
        }
    }

    @Nested
    @DisplayName("updateUser 메소드는")
    class Describe_updateUser_method {

        @Nested
        @DisplayName("만약 찾을 수 있는 사용자 id가 주어지면")
        class Context_with_can_find_userId {

            @BeforeEach
            void prepareUserModificationData() {
                given(userRepository.findByIdAndDeletedIsFalse(EXISTED_USER_ID))
                        .willReturn(Optional.of(
                                User.builder()
                                        .id(EXISTED_USER_ID)
                                        .name(USER_NAME)
                                        .email(USER_EMAIL)
                                        .password(USER_RAW_PASSWORD)
                                        .build()));
            }

            @Test
            @DisplayName("사용자 정보를 업데이트하고 리턴한다")
            void it_returns_updated_user() {
                User user = userService.updateUser(EXISTED_USER_ID, EXISTED_USER_ID, userModificationData);

                assertThat(user.getId()).isEqualTo(EXISTED_USER_ID);
                assertThat(user.getName()).isEqualTo(UPDATE_PREFIX + USER_NAME);

                verify(userRepository).findByIdAndDeletedIsFalse(EXISTED_USER_ID);
            }
        }

        @Nested
        @DisplayName("만약 찾을 수 없는 사용자 id가 주어지면")
        class Context_with_not_found_userId {

            @BeforeEach
            void prepareNotExistedUserId() {
                given(userRepository.findByIdAndDeletedIsFalse(NOT_EXISTED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("사용자를 찾을 수 없는 에러를 던진다")
            void it_returns_userNotFoundException() {
                assertThatThrownBy(() -> userService.updateUser(NOT_EXISTED_USER_ID, NOT_EXISTED_USER_ID, userModificationData))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXISTED_USER_ID);
            }
        }

        @Nested
        @DisplayName("만약 삭제된 사용자 id가 주어지면")
        class Context_with_deleted_userId {

            @BeforeEach
            void prepareDeletedUserId() {
                given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("사용자를 찾을 수 없는 에러를 던진다")
            void it_returns_userNotFoundException() {
                assertThatThrownBy(
                        () -> userService.updateUser(DELETED_USER_ID, DELETED_USER_ID, userModificationData)
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }

    @Nested
    @DisplayName("deleteUser 메소드는")
    class Describe_deleteUser_method {

        @Nested
        @DisplayName("만약 삭제되지 않고 찾을 수 있는 사용자 id가 주어지면")
        class Context_with_not_deleted_and_can_find_userId {

            @BeforeEach
            void prepareUser() {
                given(userRepository.findByIdAndDeletedIsFalse(EXISTED_USER_ID))
                        .willReturn(Optional.of(
                                User.builder()
                                        .id(EXISTED_USER_ID)
                                        .name(USER_NAME)
                                        .email(USER_EMAIL)
                                        .password(USER_RAW_PASSWORD)
                                        .build()));
            }

            @Test
            @DisplayName("사용자를 삭제하고 리턴한다")
            void it_returns_deleted_user() {
                User user = userService.deleteUser(EXISTED_USER_ID, EXISTED_USER_ID);

                assertThat(user.getId()).isEqualTo(EXISTED_USER_ID);
                assertThat(user.isDeleted()).isTrue();

                verify(userRepository).findByIdAndDeletedIsFalse(EXISTED_USER_ID);
            }
        }

        @Nested
        @DisplayName("만약 찾을 수 없는 사용자 id가 주어지면")
        class Context_with_not_found_userId {

            @Test
            @DisplayName("사용자를 찾을 수 없는 에러를 던진다")
            void it_returns_userNotFoundException() {
                assertThatThrownBy(() -> userService.deleteUser(NOT_EXISTED_USER_ID, NOT_EXISTED_USER_ID))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXISTED_USER_ID);
            }
        }

        @Nested
        @DisplayName("만약 삭제된 사용자 id가 주어지면")
        class Context_with_deleted_userId {

            @Test
            @DisplayName("사용자를 찾을 수 없는 에러를 던진다")
            void it_returns_userNotFoundException() {
                assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID, DELETED_USER_ID))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }
}
