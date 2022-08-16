package com.codesoom.assignment.controllers;

import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserInquiryInfo create(@RequestBody @Valid UserRegisterData data) {
        return null;
    }
}
