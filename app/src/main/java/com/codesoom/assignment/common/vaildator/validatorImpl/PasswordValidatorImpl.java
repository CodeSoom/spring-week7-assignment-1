package com.codesoom.assignment.common.vaildator.validatorImpl;

import com.codesoom.assignment.common.vaildator.Password;
import com.codesoom.assignment.errors.message.ErrorMessage;
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
        this.value = value;
        if (emptyCheck(context)) {
            return false;
        }
        if (sizeCheck(context)) {
            return false;
        }
        return true;
    }

    private boolean emptyCheck(ConstraintValidatorContext context) {
        boolean isEmpty = StringUtils.isEmpty(value);
        if (nullable) {
            return false;
        }
        if (isEmpty) {
            addConstraintViolation(context, ErrorMessage.PASSWORD_IS_EMPTY.getErrorMsg());
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
            message = messageMaker.append(ErrorMessage.PASSWORD_LENGTH_PREFIX.getErrorMsg())
                    .append(min)
                    .append(ErrorMessage.PASSWORD_LENGTH_MORE_THAN_SUFFIX.getErrorMsg())
                    .toString();
            addConstraintViolation(context, message);

            return true;
        }

        if (passwordLength > max) {
            message = messageMaker.append(ErrorMessage.PASSWORD_LENGTH_PREFIX.getErrorMsg())
                    .append(min)
                    .append(ErrorMessage.PASSWORD_LENGTH_BELOW_SUFFIX.getErrorMsg())
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
