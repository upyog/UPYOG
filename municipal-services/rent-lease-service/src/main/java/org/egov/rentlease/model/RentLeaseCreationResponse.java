package org.egov.rentlease.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentLeaseCreationResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo requestInfo;

	@JsonProperty("rentLease")
	private List<RentLease> rentLease;

}
