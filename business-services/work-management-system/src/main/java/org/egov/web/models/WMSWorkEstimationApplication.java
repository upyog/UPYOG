package org.egov.web.models;

import javax.validation.Valid;

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
public class WMSWorkEstimationApplication {
	
	@JsonProperty("estimate_id")
	private String estimateId=null;
	@JsonProperty("work_estimation_no")
	private String workEstimationNo=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("from_date")
	private String fromDate=null;
	@JsonProperty("to_date")
	private String toDate=null;
	@JsonProperty("estimate_type")
	private String estimateType=null;
	@JsonProperty("sor_name")
	private String sorName=null;
	@JsonProperty("download_template")
	private String downloadTemplate=null;
	@JsonProperty("upload_template")
	private String uploadTemplate=null;
	@JsonProperty("chapter")
	private String chapter=null;
	@JsonProperty("item_no")
	private String itemNo=null;
	@JsonProperty("description_of_the_item")
	private String descriptionOfTheItem=null;
	@JsonProperty("length")
	private Integer length=null;
	@JsonProperty("bw")
	private Integer bw=null;
	@JsonProperty("dh")
	private Integer dh=null;
	@JsonProperty("nos")
	private Integer noS=null;
	@JsonProperty("quantity")
	private Integer quantity=null;
	@JsonProperty("unit")
	private String unit=null;
	@JsonProperty("rate")
	private Long rate=null;
	@JsonProperty("estimate_amount")
	private Integer estimateAmount=null;
	@JsonProperty("serial_no")
	private Integer serialNo=null;
	@JsonProperty("particulars_of_item")
	private String particularsOfItem=null;
	@JsonProperty("calculation_type")
	private String calculationType=null;
	@JsonProperty("addition_deduction")
	private Integer additionDeduction=null;
	@JsonProperty("lf")
	private Integer lf=null;
	@JsonProperty("bwf")
	private Integer bwf=null;
	@JsonProperty("dhf")
	private Integer dhf=null;
	@JsonProperty("sub_total")
	private Integer subTotal=null;
	@JsonProperty("grand_total")
	private Integer grandTotal=null;
	@JsonProperty("estimated_quantity")
	private Integer estimatedQuantity=null;
	@JsonProperty("remarks")
	private String remarks=null;
	@JsonProperty("overhead_code")
	private String overheadCode=null;
	@JsonProperty("overhead_description")
	private String overheadDescription=null;
	@JsonProperty("value_type")
	private String valueType=null;
	@JsonProperty("estimated_amount")
	private Integer estimatedAmount=null;
	@JsonProperty("document_description")
	private String documentDescription=null;
	@JsonProperty("upload_document")
	private String uploadDocument=null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
	@Valid
    @JsonProperty("workflow")
    private Workflow workflow = null;
	
}
