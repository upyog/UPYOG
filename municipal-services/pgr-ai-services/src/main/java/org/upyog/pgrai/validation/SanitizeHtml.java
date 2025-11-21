package org.upyog.pgrai.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation for HTML content validation to prevent XSS attacks.
 *
 * This annotation can be applied to fields or parameters that might contain
 * HTML content and need to be validated against potential XSS vulnerabilities.
 * It uses the SanitizeHtmlValidator to perform the actual validation.
 *
 * Usage:
 * @SanitizeHtml
 * private String htmlContent;
 */

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SanitizeHtmlValidator.class)
public @interface SanitizeHtml {
    String message() default "Invalid HTML content detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
