package org.upyog.as.common;

import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Normalizes payment channel values to the digital/non-digital categories expected by the ingest service.
 */
@Component
public class PaymentChannelNormalizer {

	private static final Set<String> DIGITAL_CHANNELS = Set.of("ONLINE", "UPI", "CARD", "NETBANKING", "SYSTEM");

	/**
	 * Converts a raw payment channel into a simplified category.
	 *
	 * @param rawChannel the raw channel value from source data
	 * @return {@code Digital} when the value is recognized, otherwise {@code Non Digital}
	 */
	public String normalize(String rawChannel) {
		if (rawChannel == null) {
			return "Non Digital";
		}
		return DIGITAL_CHANNELS.contains(rawChannel.toUpperCase()) ? "Digital" : "Non Digital";
	}
}