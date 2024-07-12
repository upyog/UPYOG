package com.example.hpgarbageservice.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.hpgarbageservice.model.contract.RequestInfo;
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
	
	public List<GrbgCollectionUnit> creatingGarbageCollectionUnits;

//	public List<GrbgCollectionUnit> updatingGarbageCollectionUnits;

//	public List<GrbgCollectionUnit> deletingGarbageCollectionUnits;
	
	public List<GrbgCollectionStaff> creatingGrbgCollectionStaff;

//	public List<GrbgCollectionStaff> updatingGrbgCollectionStaff;

//	public List<GrbgCollectionStaff> deletingGrbgCollectionStaff;

	public List<GrbgCharge> creatingGrbgCharge;

}
