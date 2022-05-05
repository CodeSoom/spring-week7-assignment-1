package com.codesoom.assignment.errors;


import com.codesoom.assignment.common.CommonResponse;
import com.codesoom.assignment.errors.user.UserEmailDuplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public CommonResponse onUserEmailDuplicationException(RuntimeException e) {
        return CommonResponse.fail(e.getMessage());
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder errorMessageBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessageBuilder.append(fieldError.getDefaultMessage());
        }

        return new ResponseEntity(CommonResponse.fail(errorMessageBuilder.toString()), headers, status);
    }
}
