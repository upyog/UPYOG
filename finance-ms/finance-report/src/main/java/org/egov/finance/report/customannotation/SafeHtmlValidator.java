/**
 * 
 * 
 * @author surya
 */
package org.egov.finance.report.customannotation;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class SafeHtmlValidator implements ConstraintValidator<SafeHtml, String> {

	private static final PolicyFactory POLICY = Sanitizers.FORMATTING
		    .and(Sanitizers.LINKS)
		    .and(Sanitizers.BLOCKS)
		    .and(Sanitizers.IMAGES)
		    .and(Sanitizers.STYLES)
		    .and(Sanitizers.TABLES);

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
		    if (value == null) return true;
		    String sanitized = POLICY.sanitize(value);
		    return sanitized.equals(value); // Only allow if nothing was stripped
		}

}

