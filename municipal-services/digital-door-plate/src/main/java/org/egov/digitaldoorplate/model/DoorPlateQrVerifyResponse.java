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
public class DoorPlateQrVerifyResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	/**
	 * The snapshot as printed/encoded on the QR code, echoed back as-is.
	 */
	private DoorPlateQrSnapshot dataInQr;

	/**
	 * The corresponding current record fetched live from property-services, in
	 * the same shape, for direct comparison against {@link #dataInQr}.
	 */
	private DoorPlateQrSnapshot dataInDatabase;

	/**
	 * True only if every field matches; false means the record was updated in
	 * the system after the QR code was generated/printed.
	 */
	private Boolean isMatching;

	private List<String> mismatchedFields;
}
