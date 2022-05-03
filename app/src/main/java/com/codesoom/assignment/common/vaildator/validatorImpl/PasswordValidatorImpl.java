package com.codesoom.assignment.common.vaildator.validatorImpl;

import com.codesoom.assignment.common.vaildator.Password;
import com.codesoom.assignment.common.vaildator.error.ErrorCode;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidatorImpl implements ConstraintValidator<Password, String> {
    private String value;
    private boolean nullable;
    private boolean useSizeCheck;
    private int min;
    private int max;

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        nullable = constraintAnnotation.nullable();
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
        useSizeCheck = constraintAnnotation.useSizeCheck();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int invalidCount = 0;
        this.value = value;
        if (emptyCheck(context)) {
            invalidCount = Math.incrementExact(invalidCount);

        }
        if (sizeCheck(context)) {
            invalidCount = Math.incrementExact(invalidCount);
        }
        return invalidCount == 0;
    }

    private boolean emptyCheck(ConstraintValidatorContext context) {
        boolean isEmpty = StringUtils.isEmpty(value);
        if (nullable) {
            return false;
        }
        if (isEmpty) {
            addConstraintViolation(context, ErrorCode.EMAIL_IS_EMPTY.getErrorMsg());
        }
        return isEmpty;
    }

    private boolean sizeCheck(ConstraintValidatorContext context) {
        if (!useSizeCheck) {
            return false;
        }
        StringBuilder messageMaker = new StringBuilder();
        String message;
        int passwordLength = value.length();

        if (passwordLength < min) {
            message = messageMaker.append(ErrorCode.PASSWORD_LENGTH_PREFIX)
                    .append(min)
                    .append(ErrorCode.PASSWORD_LENGTH_MORE_THAN_SUFFIX.getErrorMsg())
                    .toString();
            addConstraintViolation(context, message);

            return true;
        }

        if (passwordLength > max) {
            message = messageMaker.append(ErrorCode.PASSWORD_LENGTH_PREFIX)
                    .append(min)
                    .append(ErrorCode.PASSWORD_LENGTH_BELOW_SUFFIX.getErrorMsg())
                    .toString();
            addConstraintViolation(context, message);
            return true;
        }

        return false;


    }


    private void addConstraintViolation(ConstraintValidatorContext context, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();
    }
}
