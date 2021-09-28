package com.codesoom.assignment.validators;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class Validators {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Validators() {
    }

    public static List<String> validate(Object target) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(target);

        if (violations.isEmpty()) {
            return Collections.emptyList();
        }

        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(toList());
    }
}
