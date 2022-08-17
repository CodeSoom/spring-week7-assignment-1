package com.codesoom.assignment.application;

import com.codesoom.assignment.Fixture;
import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import com.codesoom.assignment.errors.DuplicatedEmailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("UserService 인터페이스의")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public static final Role USER = Role.USER;
    private static final UserRegisterData REGISTER_DATA = new UserRegisterData(
            Fixture.EMAIL, Fixture.PASSWORD, Fixture.USER_NAME
    );

    private UserInquiryInfo expectInquiryInfo(Long id) {
        return new UserInquiryInfo(id, Fixture.EMAIL, Fixture.USER_NAME, USER);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("register 메서드는")
    class Describe_register {
        @Nested
        @DisplayName("유저 정보가 주어지면")
        class Context_with_userRegisterData {
            @Test
            @DisplayName("유저 조회 정보를 리턴한다")
            void It_returns_userInquiryInfo() {
                UserInquiryInfo inquiryInfo = userService.register(REGISTER_DATA);

                assertThat(inquiryInfo)
                        .isEqualTo(expectInquiryInfo(inquiryInfo.getId()));
            }
        }

        @Nested
        @DisplayName("중복된 이메일이 있다면")
        class Context_with_duplicationEmail {
            void prepare() {
                userService.register(REGISTER_DATA);
            }
            @Test
            @DisplayName("예외를 던진다")
            void It_throws_exception() {
                prepare();

                assertThatThrownBy(() -> userService.register(REGISTER_DATA))
                        .isExactlyInstanceOf(DuplicatedEmailException.class);

            }
        }
    }

}
