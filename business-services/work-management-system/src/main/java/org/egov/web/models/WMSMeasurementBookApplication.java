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
public class WMSMeasurementBookApplication {
	
	@JsonProperty("measurement_book_id")
	private String measurementBookId=null;
	@JsonProperty("work_order_no")
	private Integer workOrderNo=null;
	@JsonProperty("contractor_name")
	private String contractorName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("measurement_book_no")
	private String measurementBookNo=null;
	@JsonProperty("status")
	private String status=null;
	@JsonProperty("agreement_no")
	private Integer agreementNo=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("work_order_amount")
	private Integer workOrderAmount=null;
	@JsonProperty("work_order_date")
	private String workOrderDate=null;
	@JsonProperty("measurement_date")
	private String measurementDate=null;
	@JsonProperty("description_of_mb")
	private String descriptionOfMb=null;
	@JsonProperty("je_name")
	private String jeName=null;
	@JsonProperty("chapter")
	private String chapter=null;
	@JsonProperty("item_no")
	private String itemNo=null;
	@JsonProperty("description_of_the_item")
	private String descriptionOfTheItem=null;
	@JsonProperty("estimated_quantity")
	private Integer estimatedQuantity=null;
	@JsonProperty("cumulative_quantity")
	private Integer cumulativeQuantity=null;
	@JsonProperty("unit")
	private Integer unit=null;
	@JsonProperty("rate")
	private Long rate=null;
	@JsonProperty("consumed_quantity")
	private Integer consumedQuantity=null;
	@JsonProperty("amount")
	private Integer amount=null;
	@JsonProperty("add_mb")
	private String addMb=null;
	@JsonProperty("item_description")
	private String itemDescription=null;
	@JsonProperty("nos")
	private String nos=null;
	@JsonProperty("l")
	private String l=null;
	@JsonProperty("bw")
	private String bw=null;
	@JsonProperty("dh")
	private String dh=null;
	@JsonProperty("upload_images")
	private String uploadImages=null;
	@JsonProperty("item_code")
	private String itemCode=null;
	@JsonProperty("description")
	private String description=null;
	@JsonProperty("commulative_quantity")
	private Integer commulativeQuantity=null;
	@JsonProperty("remark")
	private String remark=null;
	@JsonProperty("overhead_description")
	private String overheadDescription=null;
	@JsonProperty("value_type")
	private String valueType=null;
	@JsonProperty("estimated_amount")
	private Integer estimatedAmount=null;
	@JsonProperty("actual_amount")
	private Integer actualAmount=null;
	@JsonProperty("document_description")
	private String documentDescription=null;
	@JsonProperty("upload_document")
	private String uploadDocument=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
