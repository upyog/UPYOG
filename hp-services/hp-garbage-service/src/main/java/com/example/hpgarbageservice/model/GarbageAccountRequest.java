package com.example.hpgarbageservice.model;

import java.util.List;

import com.example.hpgarbageservice.model.contract.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GarbageAccountRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private List<GarbageAccount> garbageAccounts;
	
}
