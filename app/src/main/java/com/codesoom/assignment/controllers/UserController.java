package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.dto.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 유저에 대한 HTTP 요청 핸들러
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
     * 주어진 유저 정보로 새로운 유저를 생성한 뒤 응답한다.
     *
     * @param registrationData 유저 정보
     * @return 생성된 유저
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResultData create(@RequestBody @Valid UserRegistrationData registrationData) {
        User user = userService.registerUser(registrationData);
        return getUserResultData(user);
    }

    /**
     * 주어진 id와 일치하는 유저를 주어진 유저 정보로 수정한 뒤 응답한다.
     *
     * @param id               유저 식별자
     * @param modificationData 유저 수정 정보
     * @return 수정된 유저
     */
    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal and authentication.credentials == null")
    UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData
    ) {
        User user = userService.updateUser(id, modificationData);
        return getUserResultData(user);
    }

    /**
     * 유저 식별자와 일치하는 유저를 삭제한다.
     *
     * @param id 유저 식별자
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostAuthorize("isAuthenticated() and #id == authentication.principal")
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
