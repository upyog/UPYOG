package com.example.hpgarbageservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class GarbageAccount {

	private Long id;

	private Long garbageId;

	private Long propertyId;

	private String type;

	private String name;

	private String mobileNumber;

	private Long parentId;

	private AuditDetails auditDetails;
	
	private List<GarbageBill> garbageBills;

	private List<GarbageAccount> childGarbageAccounts;
}