package org.upyog.as.common;

import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Maps raw usage category values from source systems to the standard values accepted by the ingest service.
 */
@Component
public class UsageCategoryNormalizer {

	private static final Set<String> VALID_CATEGORIES = Set.of("Residential", "Commercial", "Institutional",
			"Public And Semi Public", "Mixed Use", "Industrial", "Heritage", "Religious", "Recreational", "Vacant Land",
			"Others");

	/**
	 * Converts a raw usage category value into the canonical value used by downstream services.
	 *
	 * @param rawUsageCategory the usage category as stored in source data
	 * @return the normalized category label
	 */
	public String normalize(String rawUsageCategory) {
		if (rawUsageCategory == null) {
			return "Others";
		}

		String[] parts = rawUsageCategory.split("\\.");
		String topLevel = parts[0];

		if ("RESIDENTIAL".equalsIgnoreCase(topLevel)) {
			return "Residential";
		}
		if ("MIXED".equalsIgnoreCase(topLevel)) {
			return "Mixed Use";
		}

		if ("NONRESIDENTIAL".equalsIgnoreCase(topLevel) && parts.length >= 2) {
			String secondLevel = parts[1];
			return switch (secondLevel.toUpperCase()) {
			case "COMMERCIAL" -> "Commercial";
			case "INSTITUTIONAL" -> "Institutional";
			case "INDUSTRIAL" -> "Industrial";
			case "OTHERS" -> "Others";
			default -> "Others";
			};
		}

		return "Others";
	}
}