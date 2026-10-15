package org.egov.digitaldoorplate.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Trimmed view of the Property JSON returned by property-services'
 * /property/_search; only the fields needed to verify a door plate QR are
 * declared, everything else is ignored.
 */
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteProperty {

	private String propertyId;

	private String tenantId;

	private Address address;

	private List<Owner> owners;

	@AllArgsConstructor
	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Address {

		/**
		 * Holds propertyAddress, ulbName and wardNumber for the property.
		 */
		private JsonNode additionalDetails;
	}

	@AllArgsConstructor
	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Owner {

		private String propertyOwnerName;

		private String name;

		private String mobileNumber;

		private Boolean isPrimaryOwner;
	}
}
