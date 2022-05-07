package com.codesoom.assignment.controllers.user;

import com.codesoom.assignment.common.CommonResponse;
import com.codesoom.assignment.common.message.NormalMessage;
import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserService;
import com.codesoom.assignment.dto.user.UserModificationData;
import com.codesoom.assignment.dto.user.UserRegistrationData;
import com.codesoom.assignment.dto.user.UserResultData;
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
    CommonResponse create(@RequestBody @Valid UserRegistrationData registrationData) {
        userService.join(registrationData);
        return CommonResponse.success(NormalMessage.JOIN_OK.getNormalMsg());
    }

    @PatchMapping("{id}")
    UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData
    ) {
        return null;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroy(@PathVariable Long id) {
        //userService.deleteUser(id);
    }

    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
