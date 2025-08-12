package org.egov.ewst.models.event;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Represents the details of an event in the Ewaste application.
 * This class contains information such as event ID, date range, location coordinates, and address.
 */
@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class EventDetails {

	// Unique identifier for the event details
	private String id;

	// Event ID associated with the event details
	private String eventId;

	// Start date of the event
	private Long fromDate;

	// End date of the event
	private Long toDate;

	// Latitude coordinate of the event location
	private BigDecimal latitude;

	// Longitude coordinate of the event location
	private BigDecimal longitude;

	// Address of the event location
	private String address;

	/**
	 * Checks if the event details are empty.
	 * This method verifies if the essential fields (fromDate, toDate, latitude, longitude) are null.
	 *
	 * @param details the event details to check
	 * @return true if any of the essential fields are null, false otherwise
	 */
	public boolean isEmpty(EventDetails details) {
		if (null == details.getFromDate() || null == details.getToDate() || null == details.getLatitude()
				|| null == details.getLongitude()) {
			return true;
		}
		return false;
	}

}