package org.upyog.adv.web.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain model class used by advertisement service requests and responses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "tenantId", "addType", "location", "faceArea", "nightLight", "bookingDate", "bookingId" })
@ToString
public class BookingPaymentTimerDetails {

	private String bookingId;
	private String createdBy;
	private long createdTime;
	private String status;
	private String tenantId;
	private String addType;
	private String location;
	private String faceArea;
	private Boolean nightLight;
	private LocalDate bookingDate;

}
