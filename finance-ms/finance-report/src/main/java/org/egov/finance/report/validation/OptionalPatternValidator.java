

package org.egov.finance.report.validation;

import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalPatternValidator implements ConstraintValidator<OptionalPattern, Object> {

    private OptionalPattern optionalPattern;

    @Override
    public void initialize(final OptionalPattern optionalPattern) {
        this.optionalPattern = optionalPattern;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext arg1) {
        if (value == null || ObjectUtils.isEmpty(String.valueOf(value)))
            return true;
        return Pattern.compile(optionalPattern.regex(), optionalPattern.flags()).matcher(String.valueOf(value))
                .matches();
    }

}
