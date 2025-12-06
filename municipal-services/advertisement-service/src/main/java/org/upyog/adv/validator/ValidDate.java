package org.upyog.adv.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

<<<<<<< HEAD
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
=======
import javax.validation.Constraint;
import javax.validation.Payload;
>>>>>>> master-LTS

import org.upyog.adv.util.BookingUtil;

@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Invalid date format. Expected format: " + BookingUtil.DATE_FORMAT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

