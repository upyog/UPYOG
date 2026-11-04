package org.egov.infra.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Bean Validation constraint validator that checks whether a String contains
 * only HTML content permitted by the configured OWASP Java HTML Sanitizer policy.
 *
 * <p>The validator uses a combination of {@code Sanitizers.FORMATTING} and
 * {@code Sanitizers.LINKS} policies. Validation succeeds only if sanitizing
 * the input does not modify it, indicating that the content contains no
 * disallowed HTML elements or attributes.</p>
 *
 * <p>{@code null} values are considered valid. Use {@code @NotNull} in
 * conjunction with {@code @SanitizeHtml} if null values should be rejected.</p>
 */
public class SanitizeHtmlValidator implements ConstraintValidator<SanitizeHtml, String> {

    private static final PolicyFactory POLICY =
            Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String sanitized = POLICY.sanitize(value);
        // Unescape HTML entities (e.g. &#64; for '@') to allow valid emails and text while rejecting malicious HTML tags
        String unescapedSanitized = StringEscapeUtils.unescapeHtml4(sanitized);
        String unescapedValue = StringEscapeUtils.unescapeHtml4(value);
        return unescapedValue.equals(unescapedSanitized);
    }
}