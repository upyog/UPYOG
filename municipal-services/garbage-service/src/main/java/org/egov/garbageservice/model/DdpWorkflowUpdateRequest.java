package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight, uuid-keyed update for the DDP (door plate) workflow fields on
 * eg_grbg_account, used by digital-door-plate's role-specific verify/install
 * endpoints instead of the full {@link GarbageAccountRequest} update flow.
 * Only the fields relevant to the calling role are expected to be set; the
 * rest are left null and leave the existing column value untouched.
 */
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class DdpWorkflowUpdateRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private String tenantId;

	private String uuid;

	/**
	 * Vendor's print-verification outcome: "VERIFIED" or "REJECTED".
	 */
	private String vendorPrintVerified;

	private Boolean ulbVerified;

	private Boolean installationDone;

	private String ddpLatitude;

	private String ddpLongitude;

	/**
	 * Set by the printing vendor once the physical plate has been printed.
	 */
	private Boolean ddpPrintingDone;

	/**
	 * Set by the printing vendor once the printed plate has been dispatched
	 * to the ULB/installer.
	 */
	private Boolean ddpDispatched;

	/**
	 * Reason given when the vendor rejects the print (vendorPrintVerified =
	 * "REJECTED").
	 */
	private String ddpRejectionReason;

	private String remarks;

	private Boolean ddpPrintVerified;
}
