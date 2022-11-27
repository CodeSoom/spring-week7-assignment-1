package com.codesoom.assignment.user.presentation;

import com.codesoom.assignment.common.authentication.security.UserAuthentication;
import com.codesoom.assignment.common.authorization.IdVerification;
import com.codesoom.assignment.user.application.UserService;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.presentation.dto.UserModificationData;
import com.codesoom.assignment.user.presentation.dto.UserRegistrationData;
import com.codesoom.assignment.user.presentation.dto.UserResultData;
import org.springframework.http.HttpStatus;
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

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResultData create(@RequestBody @Valid final UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    @IdVerification
    UserResultData update(@PathVariable final Long id,
                          @RequestBody @Valid final UserModificationData modificationData,
                          final UserAuthentication userAuthentication) {
        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    void destroy(@PathVariable final Long id,
                 final Authentication authentication) {
        userService.deleteUser(id);
    }

    private UserResultData getUserResultData(final User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
