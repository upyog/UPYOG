package org.ksmart.birth.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = HtmlValidator.class)
@Retention(RUNTIME)
@Target({ METHOD, FIELD, CONSTRUCTOR, PARAMETER })
public @interface Html {
    String message() default "may have unsafe html content";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    HtmlValidator.List whiteListType() default HtmlValidator.List.RELAXED;
}
