package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.dto.SessionResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세션 관련 HTTP Request를 처리한다.
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
     * 인증 후 세션 키를 반환한다
     *
     * @param sessionRequestData 인증 정보
     * @return 세션 키
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseData login(
            @RequestBody SessionRequestData sessionRequestData
    ) {

        String accessToken = authenticationService.login(sessionRequestData);

        return SessionResponseData.builder()
                .accessToken(accessToken)
                .build();
    }
}
