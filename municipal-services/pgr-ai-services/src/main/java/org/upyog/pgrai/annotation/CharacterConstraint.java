package org.upyog.pgrai.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for validating character constraints on fields or methods.
 * Ensures that the annotated element does not exceed the specified size.
 */
@Documented
@Constraint(validatedBy = AdditionalDetailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CharacterConstraint {

    /**
     * The default error message to be returned when validation fails.
     *
     * @return The error message.
     */
    String message() default "Invalid Additional Details";

    /**
     * Groups for categorizing constraints.
     *
     * @return The groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload for providing additional information about the validation failure.
     *
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The maximum allowed size for the annotated element.
     *
     * @return The size constraint.
     */
    int size();
}