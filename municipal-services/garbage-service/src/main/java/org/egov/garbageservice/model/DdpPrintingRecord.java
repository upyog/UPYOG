package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One record ready for door plate printing, in the same shape as the JSON
 * embedded in the door plate's QR code: OwnerName/MobileNo/PropertyID/Address
 * come from property-services (looked up by the garbage account's
 * systemPropertyId), id is the garbage account uuid, ulbName/Ward come from
 * the garbage account's own address, and Category comes from the account's
 * (first/primary) garbage collection unit.
 */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DdpPrintingRecord {

	@JsonProperty("OwnerName")
	private String ownerName;

	@JsonProperty("MobileNo")
	private String mobileNo;

	@JsonProperty("PropertyID")
	private String propertyId;

	@JsonProperty("id")
	private String id;

	@JsonProperty("ulbName")
	private String ulbName;

	@JsonProperty("Ward")
	private String ward;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("Category")
	private String category;
	
	@JsonProperty("WardNo")
	private String wardNo; 
}
