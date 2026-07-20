package org.egov.garbageservice.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.enums.AddressType;
import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Postal address on UserV2 with type (permanent/correspondence) and geo hierarchy fields.
 * Uses AddressType enum for classification in user-service integration.
 */
@EqualsAndHashCode(of = { "id" })
public class Address {
	@CustomSafeHtml
	private String pinCode;
	@CustomSafeHtml
	private String city;
	@CustomSafeHtml
	private String address;
	private AddressType type;
	private Long id;
	@CustomSafeHtml
	private String tenantId;
	private Long userId;
	@CustomSafeHtml
	private String addressType;
	private Long LastModifiedBy;
	private Date LastModifiedDate;

	boolean isInvalid() {
		return isPinCodeInvalid() || isCityInvalid() || isAddressInvalid();
	}

	boolean isNotEmpty() {
		return StringUtils.isNotEmpty(pinCode) || StringUtils.isNotEmpty(city) || StringUtils.isNotEmpty(address);
	}

	boolean isPinCodeInvalid() {
		return pinCode != null && pinCode.length() > 10;
	}

	boolean isCityInvalid() {
		return city != null && city.length() > 300;
	}

	boolean isAddressInvalid() {
		return address != null && address.length() > 300;
	}
}
