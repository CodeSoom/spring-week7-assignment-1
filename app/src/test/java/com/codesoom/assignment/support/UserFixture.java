package com.codesoom.assignment.support;

import com.codesoom.assignment.session.presentation.dto.SessionRequestData;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.presentation.dto.UserModificationData;
import com.codesoom.assignment.user.presentation.dto.UserRegistrationData;

public enum UserFixture {
    USER_1("기범", "dev.gibeom@gmail.com", "비밀번호486"),
    USER_2("Alex", "kpmyung@gmail.com", "password486"),
    USER_INVALID_NAME("", "notExistName@no.name", "이름이 없어요"),
    USER_INVALID_EMAIL("이메일이 공백이예요", "", "이메일이 없어요"),
    USER_INVALID_PASSWORD("비밀번호가 2글자예요", "passwordInvalid@invalid.password", "hi"),
    USER_1_Wrong_PASSWORD("기범", "dev.gibeom@gmail.com", "비밀번호 틀릴거지롱"),
    ;

    private String name;
    private String email;
    private String password;

    UserFixture(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User 회원_엔티티_생성() {
        return 회원_엔티티_생성(null);
    }

    public User 회원_엔티티_생성(Long id) {
        return User.builder()
                .id(id)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }

    public UserRegistrationData 등록_요청_데이터_생성() {
        return UserRegistrationData.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }

    public UserModificationData 수정_요청_데이터_생성() {
        return UserModificationData.builder()
                .name(this.name)
                .password(this.password)
                .build();
    }

    public SessionRequestData 로그인_요청_데이터_생성() {
        return SessionRequestData.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }

    public String 이름() {
        return name;
    }

    public String 이메일() {
        return email;
    }

    public String 비밀번호() {
        return password;
    }
}
