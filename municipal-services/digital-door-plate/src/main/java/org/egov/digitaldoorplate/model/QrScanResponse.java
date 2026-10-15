package org.egov.digitaldoorplate.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrScanResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	private QrCodeData qrData;

	@JsonProperty("GarbageAccounts")
	private List<GarbageAccountScanInfo> garbageAccounts;

	/**
	 * {OwnerName, MobileNo, PropertyID, id, ulbName, Ward, Address} snapshot
	 * fetched live from property-services for the scanned account's
	 * systemPropertyId; null if the account has no linked property or
	 * property-services has no record for it (best-effort enrichment, does not
	 * fail the scan).
	 */
	@JsonProperty("property_data")
	private DoorPlateQrSnapshot propertyData;

	private Boolean alreadyCollectedToday;

	private List<GarbageCollection> todaysCollections;
}
