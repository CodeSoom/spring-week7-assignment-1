package com.codesoom.assignment.utils;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.EMAIL;
import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.NAME;
import static com.codesoom.assignment.utils.UserSampleFactory.FieldName.PASSWORD;

public class UserSampleFactory {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;
    private static final Random random = new Random();

    public static User createUser(Long id) {
        final User.UserBuilder builder = User.builder();

        System.out.println(builder.toString());

        User user = builder.id(id)
                .name(getRandomName())
                .email("test" + random.nextInt(100) + "@gmail.com")
                .build();

        user.modifyPassword(getRandomPassword(), passwordEncoder);

        return user;
    }

    public static User createUser() {
        User user = User.builder()
                .name(getRandomName())
                .email("test" + random.nextInt(100) + "@gmail.com")
                .build();

        user.modifyPassword(getRandomPassword(), passwordEncoder);

        return user;
    }

    public static UserDto.RegisterParam createRequestParam() {
        final UserDto.RegisterParam request = new UserDto.RegisterParam();
        request.setName(getRandomName());
        request.setPassword(getRandomPassword());
        request.setEmail("mailtest" + random.nextInt(100) + "@gmail.com");

        return request;
    }

    public static UserDto.UpdateParam createUpdateParam() {
        final UserDto.UpdateParam request = new UserDto.UpdateParam();
        request.setName(getRandomName());
        request.setPassword(getRandomPassword());

        return request;
    }

    public static UserDto.RegisterParam createRequestParamWith(FieldName fieldName, ValueType valueType) {
        final UserDto.RegisterParam request = new UserDto.RegisterParam();
        String testValue;

        if (valueType == ValueType.NULL) {
            testValue = null;
        } else if (valueType == ValueType.EMPTY) {
            testValue = "";
        } else {
            testValue = "  ";
        }

        request.setName(fieldName == NAME ? testValue : getRandomName());
        request.setPassword(fieldName == PASSWORD ? testValue : getRandomPassword());
        request.setEmail(fieldName == EMAIL ? testValue : "test" + random.nextInt(10000) + "@gmail.com");

        return request;
    }

    public static UserDto.UpdateParam createUpdateParamWith(FieldName fieldName, ValueType valueType) {
        final UserDto.UpdateParam request = new UserDto.UpdateParam();
        String testValue;

        if (valueType == ValueType.NULL) {
            testValue = null;
        } else if (valueType == ValueType.EMPTY) {
            testValue = "";
        } else {
            testValue = "  ";
        }

        request.setName(fieldName == NAME ? testValue : getRandomName());
        request.setPassword(fieldName == PASSWORD ? testValue : getRandomPassword());

        return request;
    }

    private static String getRandomName() {
        final List<String> lastName = Arrays.asList("김", "이", "박", "최");
        final List<String> firstName = Arrays.asList("가", "강", "건", "경", "고", "관",
                                                "광", "구", "규", "근", "기", "길",
                                                "나", "남", "노", "누", "다", "단",
                                                "달", "담", "대", "덕", "도", "동",
                                                "두", "라", "래", "로", "루", "리",
                                                "마", "만", "명", "무", "문", "미",
                                                "민", "바", "박", "백", "범", "별");
        Collections.shuffle(lastName);
        Collections.shuffle(firstName);

        return lastName.get(0) + firstName.get(0) + firstName.get(1);
    }

    private static String getRandomPassword() {
        return "randompassword" + random.nextInt(100);
    }

    public enum FieldName {
        NAME("name", "이름"),
        PASSWORD("password", "패스워드"),
        EMAIL("email", "이메일");

        private String name;
        private String description;

        FieldName(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ValueType {
        NULL, EMPTY, WHITESPACE
    }
}
