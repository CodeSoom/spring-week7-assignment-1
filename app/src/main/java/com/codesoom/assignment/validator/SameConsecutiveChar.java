package com.codesoom.assignment.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ConsecutiveCharValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SameConsecutiveChar {
    String message() default "같은 문자가 3번이상 반복되면 안됩니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
