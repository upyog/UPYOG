package org.egov.digitaldoorplate.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request shape for {@code DoorPlateController._updateDdpWorkflow}, the
 * single endpoint covering every DDP workflow role update (vendor print
 * verification, vendor printing-done, vendor dispatched, ULB verification,
 * installation); each caller only sets the field(s) relevant to its own
 * role, and they're forwarded to garbage-service's eg_grbg_account record
 * identified by {@link #garbageAccountUuid} (the "id" scanned from the door
 * plate QR / returned by {@code /door-plate/_verifyQr}).
 */
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class DoorPlateDdpWorkflowRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private String tenantId;

	private String garbageAccountUuid;

	/**
	 * Used only by {@code /door-plate/_vendorPrintVerify}: "VERIFIED" or
	 * "REJECTED".
	 */
	private String vendorPrintVerified;
	
	private String ddpRejectionReason;

	/**
	 * Used only by {@code /door-plate/_ulbVerify}.
	 */
	private Boolean ulbVerified;

	/**
	 * Along with {@link #ddpLatitude}/{@link #ddpLongitude}.
	 */
	private Boolean installationDone;

	private String ddpLatitude;

	private String ddpLongitude;

	/**
	 * Vendor step: physical plate has been printed.
	 */
	private Boolean ddpPrintingDone;

	/**
	 * Vendor step: printed plate has been dispatched to the ULB/installer.
	 */
	private Boolean ddpDispatched;
	
	private Boolean ddpPrintVerified;
}
