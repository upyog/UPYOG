package org.egov.collection.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SanitizeHtmlValidator.class)
public @interface SanitizeHtml {
    String message() default "Invalid HTML content detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
