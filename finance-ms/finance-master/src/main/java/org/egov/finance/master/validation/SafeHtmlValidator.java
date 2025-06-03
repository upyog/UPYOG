package org.egov.finance.master.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import org.egov.finance.master.validation.customannotation.SafeHtml;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

@Slf4j
public class SafeHtmlValidator implements ConstraintValidator<SafeHtml, String> {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Let @NotNull handle nulls
        String sanitized = POLICY.sanitize(value);
        log.info("Value::"+value);
        return sanitized.equals(value); // Valid only if input is already safe
    }
}

