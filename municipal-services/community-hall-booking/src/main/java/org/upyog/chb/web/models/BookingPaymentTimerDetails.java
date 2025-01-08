package org.upyog.chb.web.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "tenantId", "hallcode", "communityHallcode", "bookingDate"})
public class BookingPaymentTimerDetails {

	/**
	 * booking_id CHARACTER VARYING(64) NOT NULL UNIQUE, createdBy CHARACTER
	 * VARYING(64) NOT NULL, createdTime BIGINT NOT NULL, lastModifiedBy CHARACTER
	 * VARYING(64), lastModifiedTime BIGINT
	 */

	private String bookingId; // Maps to booking_id
	private String createdBy; // Maps to createdBy
	private long createdTime; // Maps to createdTime
	private String lastModifiedBy; // Maps to lastModifiedBy
	private Long lastModifiedTime; // Maps to lastModifiedTime (nullable)
	private String communityHallcode;
	private String hallcode;
	private String status;
	private LocalDate bookingDate;
	private String tenantId;

}
