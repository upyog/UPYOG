package org.egov.web.models;

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
public class WMSWorkOrderApplication {
	
	@JsonProperty("work_order_id")
	private String workOrderId=null;
	@JsonProperty("work_order_date")
	private String workOrderDate=null;
	@JsonProperty("agreement_no")
	private Integer agreementNo=null;
	@JsonProperty("contractor_name")
	private String contractorName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("contract_value")
	private String contractValue=null;
	@JsonProperty("stipulated_completion_period")
	private String stipulatedCompletionPeriod=null;
	@JsonProperty("tender_number")
	private Integer tenderNumber=null;
	@JsonProperty("tender_date")
	private String tenderDate=null;
	@JsonProperty("date_of_commencement")
	private String dateOfCommencement=null;
	@JsonProperty("work_assignee")
	private String workAssignee=null;
	@JsonProperty("document_description")
	private String documentDescription=null;
	@JsonProperty("terms_and_conditions")
	private String termsAndConditions=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	private String tenantId = null;
	
}
