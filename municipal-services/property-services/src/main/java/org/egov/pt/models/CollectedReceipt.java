package org.egov.pt.models;


import org.egov.pt.models.enums.Status;
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

	private  Status status;

	private AuditDetails auditDetail;

	private String tenantId;
}