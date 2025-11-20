package org.egov.vehicle.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Validator for HTML sanitization to prevent XSS attacks.
 *
 * This class implements a constraint validator that checks if a string contains
 * potentially malicious HTML content. It uses OWASP HTML Sanitizer to validate
 * input strings against a policy that allows only formatting elements and links.
 *
 * The validator returns:
 * - true if the input is null (null values are considered valid)
 * - true if the input string remains unchanged after sanitization
 * - false if the sanitized version differs from the original input
 */

@Slf4j
public class SanitizeHtmlValidator implements ConstraintValidator<SanitizeHtml, String> {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String sanitized = POLICY.sanitize(value);


        return value.equals(sanitized);
    }
}