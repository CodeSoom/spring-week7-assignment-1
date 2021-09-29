package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
import com.codesoom.assignment.errors.UnAuthorizationException;
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

/**
 * 회원 관련 HTTP Request를 처리한다.
 */
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원을 등록한다.
     *
     * @param registrationData 등록할 회원 정보
     * @return 등록된 회원 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResultData create(@RequestBody @Valid UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    /**
     * 회원 정보를 수정한다.
     *
     * @param id 회원 식별자
     * @param modificationData 수정할 회원 정보
     * @param authentication 인증 정보
     * @return 수정된 회원 정보
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData,
            Authentication authentication
    ) {
        if (id != authentication.getPrincipal()) {
            throw new UnAuthorizationException();
        }

        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    /**
     * 회원 정보를 삭제한다.
     *
     * @param id 회원 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
