package com.codesoom.assignment.domain.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserTest {

    private PasswordEncoder passwordEncoder;

    private static final Long ID = 1L;
    private static final String NAME = "홍길동";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @BeforeEach
    void setup() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void createNoArgsConstructor () {
        assertThat(new User()).isNotNull();
    }

    @DisplayName("생성자에 입력된 값들로 회원 정보를 초기화 할 수 있다.")
    @Test
    void createWithAllArgsConstructorTest() {
        final User user = new User(ID, NAME, EMAIL, PASSWORD);

        assertThat(user).isNotNull();
        assertAll(() -> {
           assertThat(user.getId()).isEqualTo(ID);
           assertThat(user.getName()).isEqualTo(NAME);
           assertThat(user.getEmail()).isEqualTo(EMAIL);
           assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(PASSWORD));
        });
    }

    @DisplayName("update 메서드는")
    @Nested
    class Describe_update {

        @DisplayName("수정할 회원 정보가 주어지면")
        @Nested
        class Context_with_update_user_data {

            private String UPDATE_PREFIX = "foo";
            private User USER_TO_UPDATE = User.withoutId(
                    UPDATE_PREFIX + NAME
                    , UPDATE_PREFIX + EMAIL
                    , UPDATE_PREFIX + PASSWORD);

            @DisplayName("수정된 결과를 반환한다.")
            @Test
            void it_returns_updated_user() {
                User user = User.withoutId(NAME, EMAIL, PASSWORD);
                user.update(USER_TO_UPDATE);

                assertThat(user.getEmail()).isEqualTo(USER_TO_UPDATE.getEmail());
                assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(USER_TO_UPDATE.getPassword()));
            }
        }
    }

    @DisplayName("authenticate 메서드는")
    @Nested
    class Describe_authenticate {

        @DisplayName("올바른 비밀번호가 주어지면")
        @Nested
        class Context_with_correct_password {
            private User user = User.withoutId(NAME, EMAIL, PASSWORD);

            @DisplayName("true를 반환한다.")
            @Test
            void will_return_true() {
                assertThat(user.authenticate(PASSWORD)).isTrue();
            }
        }

        @DisplayName("올바르지 않은 비밀번호가 주어지면")
        @Nested
        class Context_with_incorrect_password {

            private User user = User.withoutId(NAME, EMAIL, PASSWORD);
            private final String INCORRECT_PASSWORD = PASSWORD + "fail";

            @DisplayName("false를 반환한다.")
            @Test
            void will_return_false() {
                assertThat(user.authenticate(INCORRECT_PASSWORD)).isFalse();
            }
        }
    }

}
