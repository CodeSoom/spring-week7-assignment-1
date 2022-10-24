package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserCommand;
import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.dto.UserDto;
import com.codesoom.assignment.dto.UserDto.UserInfo;
import com.codesoom.assignment.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserInfo createUser(@RequestBody @Valid UserDto.RegisterParam request) {
        final UserCommand.Register command = userMapper.of(request);

        return new UserInfo(userService.registerUser(command));
    }

    @PatchMapping("{id}")
    UserInfo updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto.UpdateParam request
    ) {
        final UserCommand.Update command = userMapper.of(id, request);

        return new UserInfo(userService.updateUser(command));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
