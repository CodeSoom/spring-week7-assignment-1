package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.utils.UserSampleFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserFactory 클래스")
class UserMapperTest {

    @Nested
    @DisplayName("of(RegisterParam) 메소드는")
    class Describe_of_request_param {
        @Nested
        @DisplayName("유효한 요청 파라미터가 주어지면")
        class Context_with_valid_request_parameter {
            @Test
            @DisplayName("Register 객체를 리턴한다")
            void it_returns_register() {
                final UserDto.RegisterParam user = UserSampleFactory.createRequestParam();

                final UserCommand.Register actual = UserFactory.INSTANCE.of(user);

                assertThat(actual).isInstanceOf(UserCommand.Register.class);
            }
        }

        @Nested
        @DisplayName("요청 파라미터가 Null이면")
        class Context_with_invalid_request_parameter {
            @Test
            @DisplayName("Null을 리턴한다")
            void it_returns_register() {
                final UserCommand.Register actual = UserFactory.INSTANCE.of(null);

                assertThat(actual).isNull();
            }
        }

    }

    @Nested
    @DisplayName("of(id, UpdateParam) 메소드는")
    class Describe_of_id_and_request_param {
        @Nested
        @DisplayName("유효한 요청 파라미터가 주어지면")
        class Context_with_valid_request_parameter {
            @Test
            @DisplayName("UpdateReq 객체를 리턴한다")
            void it_returns_register() {
                final Long id = 1L;

                final UserCommand.Update actual = UserFactory.INSTANCE.of(id, UserSampleFactory.createUpdateParam());

                assertThat(actual).isInstanceOf(UserCommand.Update.class);
            }
        }

        @Nested
        @DisplayName("모든 파라미터가 Null이면")
        class Context_with_invalid_request_parameter {
            @Test
            @DisplayName("Null을 리턴한다")
            void it_returns_null() {
                final UserCommand.Update actual = UserFactory.INSTANCE.of(null, null);

                assertThat(actual).isNull();
            }
        }

        @Nested
        @DisplayName("ID가 Null이면")
        class Context_with_id_null {
            @Test
            @DisplayName("ID 필드가 Null인 객체를 리턴한다")
            void it_returns_null() {
                final UserCommand.Update actual = UserFactory.INSTANCE.of(null, UserSampleFactory.createUpdateParam());

                assertThat(actual.getId()).isNull();
                assertThat(actual.getName()).isNotNull();
                assertThat(actual.getPassword()).isNotNull();
            }
        }

        @Nested
        @DisplayName("UpdateParam이 Null이면")
        class Context_with_requestparam_null {
            @Test
            @DisplayName("UpdateParam 필드들이 Null인 객체를 리턴한다")
            void it_returns_null() {
                final Long id = 1L;

                final UserCommand.Update actual = UserFactory.INSTANCE.of(id, null);

                assertThat(actual.getId()).isNotNull();
                assertThat(actual.getName()).isNull();
                assertThat(actual.getPassword()).isNull();

            }
        }
    }

    @Nested
    @DisplayName("toEntity(UserCommand.Register) 메소드는")
    class Describe_toEntity_updaterequest {
        @Nested
        @DisplayName("유효한 파라미터가 주어지면")
        class Context_with_valid_param {
            @Test
            @DisplayName("User 객체를 리턴한다")
            void it_returns_update_request_object() {
                final Long id = 1L;
                final UserCommand.Update command = UserFactory.INSTANCE.of(id, UserSampleFactory.createUpdateParam());

                assertThat(UserFactory.INSTANCE.toEntity(command)).isInstanceOf(User.class);
            }
        }

        @Nested
        @DisplayName("입력 파라미터가 Null이면")
        class Context_with_null {
            @Test
            @DisplayName("Null을 리턴한다")
            void it_returns_user_object() {
                final UserCommand.Update command = null;

                final User actual = UserFactory.INSTANCE.toEntity(command);

                assertThat(actual).isNull();
            }
        }

    }

    @Nested
    @DisplayName("toEntity(UserCommand.Register) 메소드는")
    class Describe_toEntity_register {
        @Nested
        @DisplayName("유효한 Register 객체가 주어지면")
        class Context_with_valid_param {
            @Test
            @DisplayName("User 객체를 리턴한다")
            void it_returns_user_object() {
                final UserDto.RegisterParam user = UserSampleFactory.createRequestParam();

                final UserCommand.Register actual = UserFactory.INSTANCE.of(user);

                assertThat(UserFactory.INSTANCE.toEntity(actual)).isInstanceOf(User.class);
            }
        }

        @Nested
        @DisplayName("입력 파라미터가 Null이면")
        class Context_with_null {
            @Test
            @DisplayName("Null을 리턴한다")
            void it_returns_update_request_object() {
                final UserCommand.Register command = null;

                User actual = UserFactory.INSTANCE.toEntity(command);

                assertThat(actual).isNull();
            }
        }
    }

    @Nested
    @DisplayName("Register toEntity 메소드는")
    class Describe_register_toEntity {
        @Test
        @DisplayName("커맨드 객체를 회원 엔티티로 변환한다.")
        void it_converts_user_entity() {
            UserCommand.Register.RegisterBuilder registerBuilder = UserCommand.Register.builder();

            System.out.println(registerBuilder.toString());

            registerBuilder.name("홍길동")
                    .password("test1234")
                    .email("test@gmail.com");

            assertThat(UserFactory.INSTANCE.toEntity(registerBuilder.build())).isInstanceOf(User.class);
        }
    }

    @Nested
    @DisplayName("Update toEntity 메소드는")
    class Describe_update_request_toEntity {
        @Test
        @DisplayName("커맨드 객체를 회원 엔티티로 변환한다.")
        void it_converts_user_entity() {
            UserCommand.Update.UpdateBuilder builder = UserCommand.Update.builder();

            System.out.println(builder.toString());

            builder.id(1L)
                    .name("홍길동")
                    .password("test1234");

            assertThat(UserFactory.INSTANCE.toEntity(builder.build())).isInstanceOf(User.class);
        }
    }
}
