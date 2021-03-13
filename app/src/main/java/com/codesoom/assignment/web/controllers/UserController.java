package com.codesoom.assignment.web.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.web.dto.UserModificationData;
import com.codesoom.assignment.web.dto.UserRegistrationData;
import com.codesoom.assignment.web.dto.UserResultData;
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

    @PatchMapping("{id}")
    UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData
    ) {
        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroy(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    private UserResultData getUserResultData(User user) {
        return new UserResultData(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }
}
