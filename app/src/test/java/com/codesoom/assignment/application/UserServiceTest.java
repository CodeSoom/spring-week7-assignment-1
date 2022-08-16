package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("UserService 인터페이스의")
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    private final UserService userService = new NormalUserService(userRepository);

    public static final Role USER = Role.USER;
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String NAME = "박범진";
    private static final UserRegisterData registerData = new UserRegisterData(
            EMAIL, PASSWORD, NAME
    );

    private UserInquiryInfo expectInquiryInfo(Long id) {
        return new UserInquiryInfo(id, EMAIL, NAME, Role.USER);
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
                UserInquiryInfo inquiryInfo = userService.register(registerData);

                assertThat(inquiryInfo)
                        .isEqualTo(expectInquiryInfo(inquiryInfo.getId()));
            }
        }
    }

    private static class NormalUserService implements UserService {
        private final UserRepository userRepository;

        private NormalUserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserInquiryInfo register(UserRegisterData registerData) {
            User user = userRepository.save(registerData.toUser());
            return UserInquiryInfo.from(user);
        }
    }
}
