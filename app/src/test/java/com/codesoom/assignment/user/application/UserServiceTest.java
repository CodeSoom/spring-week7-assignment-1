package com.codesoom.assignment.user.application;

import com.codesoom.assignment.support.UserFixture;
import com.codesoom.assignment.user.application.exception.UserEmailDuplicationException;
import com.codesoom.assignment.user.application.exception.UserNotFoundException;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.domain.UserRepository;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.IdFixture.ID_MIN;
import static com.codesoom.assignment.support.UserFixture.USER_1;
import static com.codesoom.assignment.support.UserFixture.USER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("UserService 유닛 테스트")
class UserServiceTest {
    private static final Long DELETED_USER_ID = 200L;

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        userService = new UserService(mapper, userRepository);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class registerUser_메서드는 {

        @Nested
        @DisplayName("유효한 회원 정보가 주어지면")
        class Context_with_valid_user {

            @BeforeEach
            void setUp() {
                given(userRepository.existsByEmail(USER_1.이메일()))
                        .willReturn(false);

                given(userRepository.save(USER_1.회원_엔티티_생성()))
                        .will(invocation -> {
                            User user = invocation.getArgument(0);

                            return User.builder()
                                    .id(ID_MIN.value())
                                    .email(user.getEmail())
                                    .name(user.getName())
                                    .password(user.getPassword())
                                    .build();
                        });
            }

            @Test
            @DisplayName("회원을 저장하고 리턴한다")
            void it_returns_user() {
                User user = userService.registerUser(USER_1.등록_요청_데이터_생성());

                USER_이메일_이름_비밀번호_값_검증(user, USER_1);
                assertThat(user.isDeleted()).isFalse();

                verify(userRepository).existsByEmail(USER_1.이메일());
                verify(userRepository).save(USER_1.회원_엔티티_생성());
            }
        }

        @Nested
        @DisplayName("이미 등록된 이메일이 주어지면")
        class Context_with_already_exist_email {

            @BeforeEach
            void setUp() {
                given(userRepository.existsByEmail(USER_2.이메일()))
                        .willReturn(true);
            }

            @Test
            @DisplayName("UserEmailDuplicationException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.registerUser(USER_2.등록_요청_데이터_생성())
                )
                        .isInstanceOf(UserEmailDuplicationException.class);

                verify(userRepository).existsByEmail(USER_2.이메일());
                verify(userRepository, never()).save(any(User.class));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class updateUser_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(ID_MIN.value()))
                        .willReturn(
                                Optional.of(USER_1.회원_엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("회원을 수정하고 리턴한다")
            void it_returns_user() {
                User user = userService.updateUser(
                        ID_MIN.value(),
                        USER_2.수정_요청_데이터_생성()
                );

                USER_이메일_값_검증(user, USER_1);
                USER_이름_비밀번호_값_검증(user, USER_2);
                assertThat(user.isDeleted()).isFalse();

                verify(userRepository).findByIdAndDeletedIsFalse(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(ID_MAX.value()))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.updateUser(ID_MAX.value(), USER_2.수정_요청_데이터_생성())
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(ID_MAX.value());
            }
        }

        @Nested
        @DisplayName("삭제 된 회원의 id가 주어지면")
        class Context_with_deleted_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.updateUser(DELETED_USER_ID, USER_1.수정_요청_데이터_생성())
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class deleteUser_메서드는 {

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(ID_MIN.value()))
                        .willReturn(
                                Optional.of(USER_1.회원_엔티티_생성(ID_MIN.value()))
                        );
            }

            @Test
            @DisplayName("회원을 삭제 상태로 수정 후 리턴한다")
            void it_returns_user() {
                User user = userService.deleteUser(ID_MIN.value());

                assertThat(user.isDeleted()).isTrue();

                verify(userRepository).findByIdAndDeletedIsFalse(ID_MIN.value());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(ID_MAX.value()))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> userService.deleteUser(ID_MAX.value()))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(ID_MAX.value());
            }
        }

        @Nested
        @DisplayName("삭제 된 회원의 id가 주어지면")
        class Context_with_deleted_id {

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
            }
        }
    }

    private static void USER_이메일_이름_비밀번호_값_검증(User user, UserFixture userFixture) {
        USER_이메일_값_검증(user, userFixture);
        USER_이름_비밀번호_값_검증(user, userFixture);
    }

    private static void USER_이름_비밀번호_값_검증(User user, UserFixture userFixture) {
        assertThat(user.getName()).isEqualTo(userFixture.이름());
        assertThat(user.getPassword()).isEqualTo(userFixture.비밀번호());
    }

    private static void USER_이메일_값_검증(User user, UserFixture USER_1) {
        assertThat(user.getEmail()).isEqualTo(USER_1.이메일());
    }
}
