package org.egov.infra.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Bean Validation annotation used to validate HTML content against a
 * predefined sanitization policy.
 *
 * <p>Fields or parameters annotated with {@code @SanitizeHtml} are validated
 * using {@link SanitizeHtmlValidator}. Validation succeeds only when the
 * supplied HTML content contains elements and attributes permitted by the
 * configured sanitizer policy.</p>
 *
 * <p>This constraint can be applied to String fields and method parameters
 * to help prevent unsafe or unwanted HTML content from being submitted.</p>
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SanitizeHtmlValidator.class)
public @interface SanitizeHtml {

    /**
     * Validation message returned when the HTML content contains
     * disallowed elements or attributes.
     *
     * @return validation error message
     */
    String message() default "Invalid HTML content detected";

    /**
     * Allows the constraint to be assigned to one or more validation groups.
     *
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to associate
     * custom payload objects with a constraint.
     *
     * @return payload classes
     */
    Class<? extends Payload>[] payload() default {};
}