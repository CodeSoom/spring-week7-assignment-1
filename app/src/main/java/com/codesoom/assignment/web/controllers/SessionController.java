package com.codesoom.assignment.web.controllers;

import com.codesoom.assignment.core.application.AuthenticationService;
import com.codesoom.assignment.web.dto.SessionRequestData;
import com.codesoom.assignment.web.dto.SessionResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * ACCESS TOKEN을 발급하거나 검증하여 처리하거나 반환한다.
 */
@RestController
@RequestMapping("/session")
@CrossOrigin
public class SessionController {
    private AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * 로그인 데이터를 처리하고 회원이라면 토큰을 발급합니다.
     * @param sessionRequestData 로그인 요청 객체
     * @return 토큰은 담고있는 응답 객체
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseData login(
            @RequestBody SessionRequestData sessionRequestData
    ) {
        String email = sessionRequestData.getEmail();
        String password = sessionRequestData.getPassword();

        String accessToken = authenticationService.login(email, password);

        return SessionResponseData.builder()
                .accessToken(accessToken)
                .build();
    }
}
