package org.egov.receipt.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundWorkflowState {

	private String uuid;
	private String state;
	private String applicationStatus;
}