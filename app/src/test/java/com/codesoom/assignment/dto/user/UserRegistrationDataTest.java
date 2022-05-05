package com.codesoom.assignment.dto.user;

import com.codesoom.assignment.common.message.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class UserRegistrationDataTest {
    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("회원가입시 필수 값 이메일은")
    class Describe_RegistrationNeedData_Email {
        @Nested
        @DisplayName("빈 값이 주어 진다면")
        class Context_with_empty {

            @Test
            @DisplayName("[이메일은 공백이 허용되지 않습니다] 예외를 던진다.")
            void will_throw_exception() {
                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .name("eggNnuts")
                        .password("111111")
                        .build();

                String getMessage = getMessage(userRegistrationData);
                assertThat(getMessage).isEqualTo(ErrorMessage.EMAIL_IS_EMPTY.getErrorMsg());
            }
        }
    }

    @Nested
    @DisplayName("회원가입시 필수 값 이름은")
    class Describe_RegistrationNeedData_Name {
        @Nested
        @DisplayName("빈 값이 주어 진다면")
        class Context_with_empty {

            @Test
            @DisplayName("[이름은 공백이 허용되지 않습니다] 예외를 던진다.")
            void will_throw_exception() {
                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .email("dev.bslee@gmail.com")
                        .password("111111")
                        .build();

                String getMessage = getMessage(userRegistrationData);
                assertThat(getMessage).isEqualTo(ErrorMessage.NAME_IS_EMPTY.getErrorMsg());
            }
        }
    }

    @Nested
    @DisplayName("회원가입시 필수 값 비밀번호는")
    class Describe_RegistrationNeedData_Password {
        @Nested
        @DisplayName("빈 값이 주어 진다면")
        class Context_with_empty {

            @Test
            @DisplayName("[비밀번호는 허용되지 않습니다] 예외를 던진다.")
            void will_throw_exception() {
                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .name("bslee")
                        .email("dev.bslee@gmail.com")
                        .build();

                String getMessage = getMessage(userRegistrationData);
                assertThat(getMessage).isEqualTo(ErrorMessage.PASSWORD_IS_EMPTY.getErrorMsg());
            }
        }
        @Nested
        @DisplayName("설정된 자리수 이하라면")
        class Context_length_below {

            @Test
            @DisplayName("[비밀번호는 {설정된 자리수} 이상 이어야합니다}] 예외를 던진다.")
            void will_throw_exception() {
                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .name("bslee")
                        .email("dev.bslee@gmail.com")
                        .password("2")
                        .build();

                String getMessage = getMessage(userRegistrationData);
                assertThat(getMessage).contains(ErrorMessage.PASSWORD_LENGTH_MORE_THAN_SUFFIX.getErrorMsg());
             }
        }

        @Nested
        @DisplayName("설정된 자리수 이상라면")
        class Context_length_more_than {

            @Test
            @DisplayName("[비밀번호는 {설정된 자리수} 이하 이어야합니다}] 예외를 던진다.")
            void will_throw_exception() {
                UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                        .name("bslee")
                        .email("dev.bslee@gmail.com")
                        .password("22222222222222222222222222222222222222222222222222222222222222222222")
                        .build();

                String getMessage = getMessage(userRegistrationData);
                assertThat(getMessage).contains(ErrorMessage.PASSWORD_LENGTH_BELOW_SUFFIX.getErrorMsg());
            }
        }
    }

    private String getMessage(UserRegistrationData userRegistrationData) {
        Set<ConstraintViolation<UserRegistrationData>> violations = validator.validate(userRegistrationData);
        Iterator<ConstraintViolation<UserRegistrationData>> iterator = violations.iterator();
        ConstraintViolation<UserRegistrationData> violation = iterator.next();

        return violation.getMessage();
    }

}