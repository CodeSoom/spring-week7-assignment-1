package com.codesoom.assignment.controllers.session;

import com.codesoom.assignment.application.authentication.AuthenticationService;
import com.codesoom.assignment.dto.session.SessionRequestData;
import com.codesoom.assignment.dto.session.SessionResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
@CrossOrigin
public class SessionController {
//    private AuthenticationService authenticationService;
//
//    public SessionController(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public SessionResponseData login(
//            @RequestBody SessionRequestData sessionRequestData
//    ) {
//        String email = sessionRequestData.getEmail();
//        String password = sessionRequestData.getPassword();
//
//        String accessToken = authenticationService.login(email, password);
//
//        return SessionResponseData.builder()
//                .accessToken(accessToken)
//                .build();
//    }
}
