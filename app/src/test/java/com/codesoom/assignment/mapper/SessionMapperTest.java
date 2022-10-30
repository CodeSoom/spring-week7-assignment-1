package com.codesoom.assignment.mapper;

import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.SessionDto;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.utils.UserSampleFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SessionFactory 클래스")
class SessionMapperTest {

    @Nested
    @DisplayName("of(RegisterParam) 메소드는")
    class Describe_of_request_param {
        @Nested
        @DisplayName("유효한 요청 파라미터가 주어지면")
        class Context_with_valid_request_parameter {
            @Test
            @DisplayName("Register 객체를 리턴한다")
            void it_returns_register() {
                final SessionDto.SessionRequestData user = new SessionDto.SessionRequestData();
                user.setEmail("test@gmail.com");
                user.setPassword("testpassword");

                final SessionCommand.SessionRequest actual = SessionFactory.INSTANCE.of(user);

                assertThat(actual).isInstanceOf(SessionCommand.SessionRequest.class);
            }
        }

        @Nested
        @DisplayName("요청 파라미터가 Null이면")
        class Context_with_invalid_request_parameter {
            @Test
            @DisplayName("Null을 리턴한다")
            void it_returns_register() {
                final SessionCommand.SessionRequest actual = SessionFactory.INSTANCE.of(null);

                assertThat(actual).isNull();
            }
        }
    }
}
