package com.codesoom.assignment.web.controllers;

import com.codesoom.assignment.core.application.UserService;
import com.codesoom.assignment.core.domain.User;
import com.codesoom.assignment.web.dto.UserModificationData;
import com.codesoom.assignment.web.dto.UserRegistrationData;
import com.codesoom.assignment.web.dto.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 회원에 대한 요청을 처리하고 응답합니다.
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
     * 신규 회원을 등록하고, 등록한 회원을 반환합니다.
     * @param registrationData 신규 회원 정보
     * @return 등록한 회원
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResultData create(@RequestBody @Valid UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    /**
     * 회원 정보를 갱신하고, 갱신한 회원을 반환합니다.
     * @param id 회원 식별자
     * @param modificationData 갱신할 회원 정보
     * @return 갱신한 회원
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData

    ) {
        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    /**
     * 회원을 삭제합니다.
     * @param id 회원 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    void destroy(@PathVariable Long id) {
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
