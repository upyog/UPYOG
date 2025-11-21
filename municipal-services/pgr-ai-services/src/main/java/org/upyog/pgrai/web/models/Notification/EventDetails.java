package org.upyog.pgrai.web.models.Notification;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Represents the details of an event in the notification system.
 * This class encapsulates additional information about an event, including:
 * - The unique ID of the event details.
 * - The event ID to which these details belong.
 * - The start and end dates for the event.
 * - The geographical location of the event (latitude and longitude).
 * - The address associated with the event.
 *
 * This class is part of the notification module in the PGR system.
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
	
	private String id;
	
	private String eventId;

	private Long fromDate;
	
	private Long toDate;
	
	private BigDecimal latitude;
	
	private BigDecimal longitude;
	
	private String address;
	
	
	public boolean isEmpty(EventDetails details) {
		return null == details.getFromDate() || null == details.getToDate() || null == details.getLatitude() || null == details.getLongitude();
	}
	
}
