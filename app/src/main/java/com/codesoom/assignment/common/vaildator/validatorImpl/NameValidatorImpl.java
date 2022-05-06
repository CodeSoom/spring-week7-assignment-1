package com.codesoom.assignment.common.vaildator.validatorImpl;

import com.codesoom.assignment.common.vaildator.Name;
import com.codesoom.assignment.common.message.ErrorMessage;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidatorImpl implements ConstraintValidator<Name, String> {
    private String value;
    private boolean nullable;

    @Override
    public void initialize(Name constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        this.value = value;
        if (emptyCheck(context)) {

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
            addConstraintViolation(context, ErrorMessage.NAME_IS_EMPTY.getErrorMsg());
        }
        return isEmpty;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();
    }
}
