package com.codesoom.assignment.controllers.auth;

import com.codesoom.assignment.common.CommonResponse;
import com.codesoom.assignment.common.message.NormalMessage;
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
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommonResponse<AuthResponseData> login(@RequestBody @Valid AuthRequestData request) {
        String accessToken = authenticationService.login(request);
        AuthResponseData authResponseData = new AuthResponseData(accessToken);
        StringBuilder loginSuccessBuilder = new StringBuilder();
        String loginSuccessMessage = loginSuccessBuilder.append("[")
                                                        .append(request.getEmail()).append("]님")
                                                        .append(NormalMessage.LOGIN_OK.getNormalMsg())
                                                        .toString();

        return CommonResponse.success(authResponseData,loginSuccessMessage);
    }

}
