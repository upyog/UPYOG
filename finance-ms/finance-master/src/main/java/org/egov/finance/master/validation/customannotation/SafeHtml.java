package org.egov.finance.master.validation.customannotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import org.egov.finance.master.validation.SafeHtmlValidator;

@Documented
@Constraint(validatedBy = SafeHtmlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeHtml {
    String message() default "Unsafe HTML content";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

