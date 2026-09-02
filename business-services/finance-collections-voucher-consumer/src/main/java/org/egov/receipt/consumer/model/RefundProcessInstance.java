package org.egov.receipt.consumer.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundProcessInstance {

	private String id;
	private String tenantId;
	private String businessService;
	private String businessId;
	private String action;
	private String moduleName;
	private RefundWorkflowState state;
	private String comment;
	private List<Object> documents;
	private List<String> assignes;
}