package com.codesoom.assignment.web.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.web.dto.UserModificationData;
import com.codesoom.assignment.web.dto.UserRegistrationData;
import com.codesoom.assignment.web.dto.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("isAuthenticated()")
    UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData,
            Authentication authentication
    ) {
        if (!id.equals(authentication.getPrincipal())) {
            System.out.println("=======");
            System.out.println(id);
            System.out.println(authentication.getPrincipal());
            System.out.println(id.equals(authentication.getPrincipal()));
            System.out.println("=======");
            throw new AccessDeniedException("다른 유저의 정보를 수정할 수 없습니다.");
        }
        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
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
