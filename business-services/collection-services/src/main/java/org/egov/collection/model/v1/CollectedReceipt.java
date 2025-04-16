package org.egov.collection.model.v1;

import org.egov.collection.model.AuditDetails;
import org.egov.collection.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectedReceipt {

	private String businessService;

	private String consumerCode;

	private String receiptNumber;

	private Double receiptAmount;

	private Long receiptDate;

	private Status status;

	private AuditDetails auditDetail;

	private String tenantId;
}