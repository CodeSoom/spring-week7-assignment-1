package com.codesoom.assignment.domain;

import com.codesoom.assignment.dependency.Container;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    EncryptionService encryptionService = Container.encryptionService();

    @Test
    void changeWith() {
        User user = new User(1L, "test user", "aaa@bbb.ccc", "test");

        user.changeWith("TEST", "TEST");

        assertThat(user.getName()).isEqualTo("TEST");
        assertThat(user.getPassword()).isEqualTo(encryptionService.encryptedValue("TEST"));
    }

    @Test
    void destroy() {
        User user = new User(1L, "test user", "aaa@bbb.ccc", "test");

        assertThat(user.isDestroyed()).isFalse();

        user.destroy();

        assertThat(user.isDestroyed()).isTrue();
    }

    @Test
    void authenticate() {
        User user = new User(1L, "aaa@bbb.ccc", "test user", "test");

        assertThat(user.authenticate("test")).isTrue();
        assertThat(user.authenticate("xxx")).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = new User(1L, "aaa@bbb.ccc", "test user", "test password");
        user.destroy();

        assertThat(user.authenticate("test")).isFalse();
        assertThat(user.authenticate("xxx")).isFalse();
    }

    @Test
    void createUser() {
        User user1 = new User(1L, "aaa@bbb.ccc", "test user", "test password");
        assertThat(user1).isNotNull();

        User user2 = new User();
        assertThat(user2).isNotNull();
    }
}
