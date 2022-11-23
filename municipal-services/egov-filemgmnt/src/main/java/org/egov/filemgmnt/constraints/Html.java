package org.egov.filemgmnt.constraints;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

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
