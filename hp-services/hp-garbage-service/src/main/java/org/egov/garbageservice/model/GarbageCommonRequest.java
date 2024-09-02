package org.egov.garbageservice.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GarbageCommonRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private List<GrbgCollectionUnit> creatingGarbageCollectionUnits;

//	private List<GrbgCollectionUnit> updatingGarbageCollectionUnits;

//	private List<GrbgCollectionUnit> deletingGarbageCollectionUnits;
	
	private List<GrbgCollectionStaff> creatingGrbgCollectionStaff;

//	private List<GrbgCollectionStaff> updatingGrbgCollectionStaff;

//	private List<GrbgCollectionStaff> deletingGrbgCollectionStaff;

	private List<GrbgCharge> creatingGrbgCharge;

	private List<GrbgOldDetails> creatingGrbgOldDetails;

	private List<GrbgScheduledRequests> creatingGrbgScheduledRequests;

	private List<GrbgDeclaration> creatingGrbgDeclaration;

}
