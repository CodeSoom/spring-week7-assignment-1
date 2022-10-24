package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.dto.SessionDto;
import com.codesoom.assignment.dto.SessionDto.SessionInfo;
import com.codesoom.assignment.mapper.SessionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
@CrossOrigin
public class SessionController {
    private final AuthenticationService authenticationService;

    private final SessionMapper sessionMapper;

    public SessionController(AuthenticationService authenticationService, SessionMapper sessionMapper) {
        this.authenticationService = authenticationService;
        this.sessionMapper = sessionMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionInfo login(
            @RequestBody SessionDto.SessionRequestData request
    ) {
        final SessionCommand.SessionRequest command = sessionMapper.of(request);

        return new SessionInfo(authenticationService.login(command));
    }
}
