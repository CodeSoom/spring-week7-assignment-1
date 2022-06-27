package com.codesoom.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청에 사용하는 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequestData {
    private String email;
    private String password;
}
