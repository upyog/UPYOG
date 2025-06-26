package org.egov.finance.voucher.validation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueCheckValidator.class)
public @interface Unique {
    String[] fields() default {};

    String id() default "id";

    String tableName() default "";

    String[] columnName() default {};

    String message() default "{validator.unique}";

    boolean enableDfltMsg() default false;

    boolean isSuperclass() default false;
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}