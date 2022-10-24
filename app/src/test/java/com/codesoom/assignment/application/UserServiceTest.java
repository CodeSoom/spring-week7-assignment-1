package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.mapper.UserMapper;
import com.codesoom.assignment.utils.UserSampleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {
    @DataJpaTest
    class JpaTest {
        @Autowired
        private UserRepository repository;
        private UserService service;

        private final UserMapper userMapper = UserMapper.INSTANCE;

        public UserMapper getUserMapper() {
            return userMapper;
        }

        public UserRepository getUserRepository() {
            return repository;
        }

        public UserService getUserService() {
            if (service == null) {
                service = new UserService(repository, userMapper);
            }
            return service;
        }
    }

    @Nested
    @DisplayName("registerUser 메소드는")
    class Describe_registerUser {
        @Nested
        @DisplayName("새로운 회원정보가 주어지면")
        class Context_with_new_user_info extends JpaTest {
            private final UserCommand.Register givenCommand = getUserMapper().of(UserSampleFactory.createRequestParam());

            @Test
            @DisplayName("등록하고 회원정보를 리턴한다.")
            void it_returns_registered_member() {
                final User savedUser = getUserService().registerUser(givenCommand);

                assertThat(savedUser.getEmail()).isEqualTo(givenCommand.getEmail());
                assertThat(savedUser.getName()).isEqualTo(givenCommand.getName());
            }
        }

        @Nested
        @DisplayName("중복된 이메일이 주어지면")
        class Context_with_duplicated_email extends JpaTest {
            private UserCommand.Register givenUser;

            @BeforeEach
            void prepare() {
                final User savedUser = getUserRepository().save(UserSampleFactory.createUser());

                final UserDto.RegisterParam user = new UserDto.RegisterParam();
                user.setEmail(savedUser.getEmail());
                user.setName("홍길동");
                user.setPassword("test1234");

                givenUser = getUserMapper().of(user);
            }

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> getUserService().registerUser(givenUser))
                        .isInstanceOf(UserEmailDuplicationException.class);
            }
        }
    }

    @Nested
    @DisplayName("updateUser 메소드는")
    class Describe_updateUser {
        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id extends JpaTest {
            private User savedUser;

            @BeforeEach
            void prepare() {
                savedUser = getUserRepository().save(UserSampleFactory.createUser());
            }

            @Test
            @DisplayName("회원정보를 수정하고 리턴한다.")
            void it_returns_modified_user() {
                final UserCommand.Update givenCommand = UserCommand.Update.builder()
                        .id(savedUser.getId())
                        .name("홍길동")
                        .password("modify_test1234")
                        .build();

                User updatedUser = getUserService().updateUser(givenCommand);

                assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
                assertThat(updatedUser.getName()).isEqualTo(givenCommand.getName());
                assertThat(updatedUser.getPassword()).isEqualTo(givenCommand.getPassword());
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id extends JpaTest {
            private final Long INVALID_USER_ID = 9999L;
            private final UserCommand.Update givenCommand = UserMapper.INSTANCE.of(INVALID_USER_ID, UserSampleFactory.createUpdateParam());

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> getUserService().updateUser(givenCommand))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("삭제된 ID가 주어지면")
        class Context_with_deleted_id extends JpaTest {
            private User deletedUser;

            @BeforeEach
            void prepare() {
                final User savedUser = getUserRepository().save(UserSampleFactory.createUser());
                deletedUser = getUserService().deleteUser(savedUser.getId());
            }

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                final UserCommand.Update givenCommand = UserCommand.Update.builder()
                        .id(deletedUser.getId())
                        .name("홍길동")
                        .password("modify_test1234")
                        .build();

                assertThat(deletedUser.isDeleted()).isTrue();

                assertThatThrownBy(() -> getUserService().updateUser(givenCommand))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteUser 메소드는")
    class Describe_deleteUser {
        @Nested
        @DisplayName("유효한 ID가 주어지면")
        class Context_with_valid_id extends JpaTest {
            private User savedUser;

            @BeforeEach
            void prepare() {
                savedUser = getUserRepository().save(UserSampleFactory.createUser());
            }

            @Test
            @DisplayName("회원정보를 삭제하고 리턴한다.")
            void it_returns_deleted_user() {
                User user = getUserService().deleteUser(savedUser.getId());

                assertThat(user.getId()).isEqualTo(savedUser.getId());
                assertThat(user.isDeleted()).isTrue();
            }
        }

        @Nested
        @DisplayName("유효하지않은 ID가 주어지면")
        class Context_with_invalid_id extends JpaTest {
            private final Long INVALID_USER_ID = 9999L;

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> getUserService().deleteUser(INVALID_USER_ID))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("삭제된 ID가 주어지면")
        class Context_with_deleted_id extends JpaTest {
            private User deletedUser;

            @BeforeEach
            void prepare() {
                final User savedUser = getUserRepository().save(UserSampleFactory.createUser());
                deletedUser = getUserService().deleteUser(savedUser.getId());
            }

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> getUserService().deleteUser(deletedUser.getId()))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }
}
