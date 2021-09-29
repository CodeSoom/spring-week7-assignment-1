package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.ForbiddenRequestException;
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

class UserServiceTest {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    private UserRegistrationData registrationDataFixture;
    private User userFixture;
    private UserModificationData modificationDataFixture;

    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setupInstance() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(mapper, userRepository, passwordEncoder);
    }

    @BeforeEach
    void setupFixture() {
        registrationDataFixture = UserRegistrationData.builder()
                .email("tester@example.com")
                .name("Tester")
                .password("test")
                .build();

        userFixture = User.builder()
                .id(1L)
                .email("tester@example.com")
                .name("Tester")
                .build();

        modificationDataFixture = UserModificationData.builder()
                .name("UPDATED")
                .password("UPDATED")
                .build();
    }

    @Nested
    @DisplayName("회원 등록")
    class UserRegistration {
        @BeforeEach
        void mockRepository() {
            given(userRepository.existsByEmail("tester@example.com")).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(userFixture);
        }

        @Nested
        @DisplayName("유효한 이메일과 비밀번호이면")
        class WithValidEmailAndPassword {
            @Test
            @DisplayName("회원을 등록하고 등록된 회원을 리턴한다.")
            void registerUser() {
                User user = userService.registerUser(registrationDataFixture);

                assertThat(user.getId()).isEqualTo(userFixture.getId());
                assertThat(user.getEmail()).isEqualTo(userFixture.getEmail());
                assertThat(user.getName()).isEqualTo(userFixture.getName());

                verify(userRepository).save(any(User.class));
            }
        }

        @Nested
        @DisplayName("중복된 이메일인 경우")
        class WithDuplicatedEmail {
            @BeforeEach
            void mockRepository() {
                given(userRepository.existsByEmail("tester@example.com")).willReturn(true);
                given(userRepository.save(any(User.class))).willReturn(userFixture);
            }

            @Test
            void registerUserWithDuplicatedEmail() {
                assertThatThrownBy(() -> userService.registerUser(registrationDataFixture))
                        .isInstanceOf(UserEmailDuplicationException.class);

                verify(userRepository).existsByEmail("tester@example.com");
            }
        }
    }

    @Nested
    @DisplayName("회원 수정")
    class UserUpdate {
        @BeforeEach
        void mockRepository() {
            given(userRepository.findByIdAndDeletedIsFalse(1L))
                .willReturn(Optional.of(userFixture));

            given(userRepository.findByIdAndDeletedIsFalse(100L))
                    .willReturn(Optional.empty());
        }

        @Nested
        @DisplayName("찾을 수 있는 회원 식별자로 수정요청을 하면")
        class WithFoundUserId {
            @Test
            @DisplayName("회원을 수정하고 수정된 회원을 반환한다.")
            void updateUserAndReturnIt() {
                User user = userService.updateUser(1L, modificationDataFixture);

                assertThat(user.getId()).isEqualTo(1L);
                assertThat(user.getEmail()).isEqualTo(userFixture.getEmail());
                assertThat(user.getName()).isEqualTo(modificationDataFixture.getName());
                assertThat(user.getName()).isEqualTo(modificationDataFixture.getPassword());

                verify(userRepository).findByIdAndDeletedIsFalse(1L);
            }
        }

        @Nested
        @DisplayName("해당 식별자의 회원을 찾을 수 없거나 삭제된 회원이면")
        class WithNotFoundUserId {
            @Test
            @DisplayName("에러를 반환한다.")
            void updateUserWithNotExistedId() {
                assertThatThrownBy(() -> userService.updateUser(100L, modificationDataFixture))
                        .isInstanceOf(ForbiddenRequestException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(100L);
            }
        }
    }

    @Nested
    @DisplayName("회원 삭제")
    class UserDeletion {
        @BeforeEach
        void mockRepository() {
            given(userRepository.findByIdAndDeletedIsFalse(1L))
                    .willReturn(Optional.of(userFixture));

            given(userRepository.findByIdAndDeletedIsFalse(100L))
                    .willReturn(Optional.empty());
        }

        @Nested
        @DisplayName("찾을 수 있는 회원 식별자로 삭제요청을 하면")
        class WithFoundUserId {
            @Test
            @DisplayName("회원을 삭제하고 삭제된 회원을 반환한다.")
            void updateUserAndReturnIt() {
                User user = userService.deleteUser(1L);

                assertThat(user.getId()).isEqualTo(1L);
                assertThat(user.isDeleted()).isTrue();

                verify(userRepository).findByIdAndDeletedIsFalse(1L);

            }
        }

        @Nested
        @DisplayName("해당 식별자의 회원을 찾을 수 없거나 삭제된 회원이면")
        class WithNotFoundUserId {
            @Test
            @DisplayName("에러를 반환한다.")
            void updateUserWithNotExistedId() {
                assertThatThrownBy(() -> userService.deleteUser(100L))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(100L);
            }
        }
    }
}
