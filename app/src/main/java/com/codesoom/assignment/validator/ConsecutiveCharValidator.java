package com.codesoom.assignment.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 같은 문자가 3번이상 반복되는지 체크한다.
 */
public class ConsecutiveCharValidator implements ConstraintValidator<SameConsecutiveChar, String> {
    private static final String REGEX = "(\\w)\\1\\1";

    @Override
    public void initialize(SameConsecutiveChar constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !Pattern.compile(REGEX).matcher(value).find();
    }
}
