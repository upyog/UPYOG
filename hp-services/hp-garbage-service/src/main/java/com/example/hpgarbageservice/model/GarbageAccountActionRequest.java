package com.example.hpgarbageservice.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GarbageAccountActionRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;
    
	private List<String> applicationNumbers;
	
}
