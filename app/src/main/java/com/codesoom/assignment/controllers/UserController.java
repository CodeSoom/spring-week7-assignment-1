package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.dto.UserDto.UserInfo;
import com.codesoom.assignment.mapper.UserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserFactory userFactory;

    public UserController(UserService userService, AuthenticationService authenticationService, UserFactory userFactory) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userFactory = userFactory;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserInfo createUser(@RequestBody @Valid UserDto.RegisterParam request) {
        final UserCommand.Register command = userFactory.of(request);

        return new UserInfo(userService.registerUser(command));
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    UserInfo updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto.UpdateParam request,
            Authentication authentication
    ) {
        if (!authentication.getPrincipal().equals(id)) {
            throw new AccessDeniedException("Access Denied");
        }
        System.out.println("id = " + id);
        final UserCommand.Update command = userFactory.of(id, request);

        return new UserInfo(userService.updateUser(command));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    void deleteUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        userService.deleteUser(id);
    }

}
