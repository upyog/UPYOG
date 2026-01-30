package org.egov.collection.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Constraint validator for the {@link SanitizeHtml} annotation.
 * <p>
 * This validator uses the OWASP Java HTML Sanitizer to check if a string contains
 * unsafe or unwanted HTML content, helping to prevent XSS and HTML/script injection attacks.
 * <p>
 * The applied policy allows only basic formatting elements and links (see {@link Sanitizers#FORMATTING} and {@link Sanitizers#LINKS}).
 * <ul>
 *   <li>Returns {@code true} if the input is {@code null} (null values are considered valid).</li>
 *   <li>Returns {@code true} if the input string remains unchanged after sanitization.</li>
 *   <li>Returns {@code false} if the sanitized version differs from the original input, indicating unsafe HTML content.</li>
 * </ul>
 * <p>
 * Usage: This validator is automatically invoked for fields annotated with {@link SanitizeHtml}.
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
