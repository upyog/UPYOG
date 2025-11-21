package org.upyog.sv.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import org.upyog.sv.util.StreetVendingUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Invalid date format. Expected format: " + StreetVendingUtil.DATE_FORMAT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

