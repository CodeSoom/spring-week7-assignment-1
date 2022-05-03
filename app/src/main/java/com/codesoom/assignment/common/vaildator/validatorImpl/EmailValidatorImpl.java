package com.codesoom.assignment.common.vaildator.validatorImpl;

import com.codesoom.assignment.common.vaildator.Email;
import com.codesoom.assignment.errors.message.ErrorMessage;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidatorImpl implements ConstraintValidator<Email, String> {
    private String value;
    private boolean nullable;

    @Override
    public void initialize(Email constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int invalidCount = 0;
        this.value = value;
        if (emptyCheck()) {
            invalidCount = Math.incrementExact(invalidCount);
            addConstraintViolation(context, ErrorMessage.EMAIL_IS_EMPTY.getErrorMsg());
        }
        return invalidCount == 0;
    }

    private boolean emptyCheck() {
        boolean isEmpty = StringUtils.isEmpty(value);
        return isEmpty;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();
    }
}
