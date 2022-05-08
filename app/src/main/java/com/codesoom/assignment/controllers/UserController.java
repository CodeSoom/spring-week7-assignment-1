package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.authorization.AuthorizeUser;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResultData create(@RequestBody @Valid UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    @PatchMapping("{userId}")
    @AuthorizeUser
    UserResultData update(
            @PathVariable Long userId,
            @RequestBody @Valid UserModificationData modificationData
    ) {
        User user = userService.updateUser(userId, modificationData);
        return getUserResultData(user);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AuthorizeUser
    void destroy(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
