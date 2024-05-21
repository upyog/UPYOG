package com.example.hpgarbageservice.model;

import com.example.hpgarbageservice.model.contract.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GarbageAccountRequest {

	private RequestInfo requestInfo;
	
	private GarbageAccount garbageAccount;
	
}
