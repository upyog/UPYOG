package org.upyog.chb.web.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "tenantId", "hallcode", "communityHallcode", "bookingDate", "bookingId"})
@ToString
public class BookingPaymentTimerDetails {

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
