package com.codesoom.assignment.validators;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

public class Validators {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    public static final String EMPTY_STR = "";

    private Validators() {
    }

    public static String getValidateResults(Object target) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(target);

        if (violations.isEmpty()) {
            return EMPTY_STR;
        }

        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
    }
}
