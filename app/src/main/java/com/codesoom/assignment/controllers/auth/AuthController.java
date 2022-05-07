package com.codesoom.assignment.controllers.auth;

import com.codesoom.assignment.common.CommonResponse;
import com.codesoom.assignment.domain.auth.AuthenticationService;
import com.codesoom.assignment.dto.auth.AuthRequestData;
import com.codesoom.assignment.dto.auth.AuthResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/session")
@CrossOrigin
public class AuthController {
    private AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommonResponse<AuthResponseData> login(@Valid @RequestBody AuthRequestData request) {
        String accessToken = authenticationService.login(request);
        AuthResponseData authResponseData = new AuthResponseData(accessToken);
        return CommonResponse.success(authResponseData);
    }

}
