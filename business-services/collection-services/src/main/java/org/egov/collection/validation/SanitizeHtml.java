package org.egov.collection.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Custom validation annotation for sanitizing HTML content in string fields.
 * <p>
 * This annotation can be applied to fields or method parameters to ensure that
 * the value does not contain unsafe or unwanted HTML content. It uses the
 * {@link SanitizeHtmlValidator} to check if the input is safe according to the
 * configured OWASP HTML sanitization policy. If the value contains HTML tags or
 * content that would be sanitized, validation will fail and an error will be thrown.
 * <p>
 * Usage example:
 * <pre>
 *     @SanitizeHtml
 *     private String userInput;
 * </pre>
 *
 * This helps prevent XSS and other HTML/script injection vulnerabilities.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SanitizeHtmlValidator.class)
public @interface SanitizeHtml {
    String message() default "Invalid HTML content detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
