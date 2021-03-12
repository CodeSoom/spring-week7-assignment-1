package com.codesoom.assignment.application;

import com.codesoom.assignment.security.UserAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

class GuardTest {

    @DisplayName("인증객체의 사용자 id와 주어진 사용자 id 동일 여부를 비교한다")
    @Test
    void compareUserId() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        Authentication authentication = new UserAuthentication(userId1);
        Guard guard = new Guard();

        assertThat(guard.checkIdMatch(authentication, userId2)).isFalse();
        assertThat(guard.checkIdMatch(null, userId2)).isFalse();
    }
}
