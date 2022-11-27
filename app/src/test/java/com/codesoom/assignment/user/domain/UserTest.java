package com.codesoom.assignment.user.domain;

import com.codesoom.assignment.support.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.codesoom.assignment.support.UserFixture.USER_1;
import static com.codesoom.assignment.support.UserFixture.USER_2;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("User 빌더 테스트")
    void builder() {
        User user = USER_1.회원_엔티티_생성();

        USER_ID_제외_모든_값_검증(user, USER_1);
    }

    @Test
    @DisplayName("User 수정 테스트")
    void changeWith() {
        User user = USER_1.회원_엔티티_생성();

        user.changeWith(USER_2.회원_엔티티_생성());

        assertThat(user.getName()).isEqualTo(USER_2.이름());
        assertThat(user.getPassword()).isNotEqualTo(USER_2.비밀번호());
    }

    @Test
    @DisplayName("User password 수정 테스트")
    void changePassword() {
        User user = USER_1.회원_엔티티_생성();

        user.changePassword(USER_2.비밀번호(), passwordEncoder);

        assertThat(user.getPassword()).isNotEqualTo(USER_2.비밀번호());
    }

    @Test
    @DisplayName("User 삭제 테스트")
    void destroy() {
        User user = USER_1.회원_엔티티_생성();

        assertThat(user.isDeleted()).isFalse();

        user.destroy();

        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("User 로그인 인증 테스트")
    void authenticate() {
        User user = USER_1.회원_엔티티_생성();

        user.changePassword(USER_1.비밀번호(), passwordEncoder);

        assertThat(user.authenticate(USER_1.비밀번호(), passwordEncoder)).isTrue();
        assertThat(user.authenticate(USER_2.비밀번호(), passwordEncoder)).isFalse();
    }

    @Test
    @DisplayName("삭제된 User 로그인 인증 테스트")
    void authenticateWithDeletedUser() {
        User user = USER_1.회원_엔티티_생성();

        user.changePassword(USER_1.비밀번호(), passwordEncoder);

        user.destroy();

        assertThat(user.authenticate(USER_1.비밀번호(), passwordEncoder)).isFalse();
    }

    @Test
    @DisplayName("객체 비교 테스트")
    void equals_and_hashcode() {
        User user1 = USER_2.회원_엔티티_생성();
        User user2 = USER_2.회원_엔티티_생성();

        assertThat(user1).isEqualTo(user2);
    }


    private static void USER_ID_제외_모든_값_검증(User user, UserFixture userFixture) {
        assertThat(user.getName()).isEqualTo(userFixture.이름());
        assertThat(user.getEmail()).isEqualTo(userFixture.이메일());
        assertThat(user.getPassword()).isEqualTo(userFixture.비밀번호());
        assertThat(user.isDeleted()).isFalse();
    }
}
