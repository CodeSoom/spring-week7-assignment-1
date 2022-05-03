package com.codesoom.assignment.errors;


import com.codesoom.assignment.common.CommonResponse;
import com.codesoom.assignment.errors.user.UserEmailDuplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public CommonResponse onUserEmailDuplicationException(RuntimeException e){
        return CommonResponse.fail(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResponse onConstraintViolationException(ConstraintViolationException e) {
        return null;
    }
}
