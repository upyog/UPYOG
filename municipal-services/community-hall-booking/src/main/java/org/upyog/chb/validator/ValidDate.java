package org.upyog.chb.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.upyog.chb.util.CommunityHallBookingUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Invalid date format. Expected format: " + CommunityHallBookingUtil.DATE_FORMAT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

