/**
 * 
 * 
 * @author surya
 */
package org.egov.finance.master.customannotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SafeHtmlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeHtml {
    String message() default "Unsafe HTML content";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

