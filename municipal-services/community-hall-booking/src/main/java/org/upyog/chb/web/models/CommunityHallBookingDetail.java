package org.upyog.chb.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.upyog.chb.web.models.workflow.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of community halls
 */
@ApiModel(description = "Details for new booking of community halls")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallBookingDetail {
	
	@JsonProperty("bookingId")
	private String bookingId = null;
	
	private String bookingNo;
	
	private Long bookingDate;
	
	@NotBlank(message = "CHB_BLANK_APPLICANT_NAME")
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String applicantName;
	
	@NotBlank
	@Size(min = 10, max = 10)
	private String applicantMobileNo;
	
	@NotBlank
	@Size(min = 10, max = 10)
	private String applicantAlternateMobileNo;
	
	@NotBlank
	@Email
	private String applicantEmailId;
	
	@NotBlank
	@JsonProperty("tenantId")
	private String tenantId = null;
	
	@JsonProperty("communityHallId")
	private Integer communityHallId = null;
	
	private String communityHallName;
	
	@JsonProperty("bookingStatus")
	private String bookingStatus;

	@Valid
	@NotNull
	@JsonProperty("residentType")
	private ResidentType residentType = null;

	@Valid
	@NotNull
	@JsonProperty("specialCategory")
	private SpecialCategory specialCategory = null;

	@Valid
	@NotNull
	@JsonProperty("purpose")
	private BookingPurpose purpose = null;
	
	@NotBlank
	private String purposeDescription;

	@NotBlank
	@JsonProperty("eventName")
	private String eventName = null;

	@NotBlank
	@JsonProperty("eventOrganisedBy")
	private String eventOrganisedBy = null;
	
	@NotNull
	@JsonProperty("bookingSlotDetails")
	private List<BookingSlotDetail> bookingSlotDetails;
	
	@JsonProperty("documents")
	@Valid
	private List<DocumentDetails> uploadedDocumentDetails = null;
	
	@Valid
	@JsonProperty("bankDetails")
	private BankDetails bankDetails = null;
	
	private AuditDetails auditDetails;
	
	@JsonProperty("workflow")
 	private ProcessInstance workflow = null;
	

	public CommunityHallBookingDetail addUploadedDocumentDetailsItem(DocumentDetails uploadedDocumentDetailsItem) {
		if (this.uploadedDocumentDetails == null) {
			this.uploadedDocumentDetails = new ArrayList<>();
		}
		this.uploadedDocumentDetails.add(uploadedDocumentDetailsItem);
		return this;
	}


	public CommunityHallBookingDetail addBookingSlots(BookingSlotDetail bookingSlotDetail) {
		if(bookingSlotDetails == null){
			bookingSlotDetails = new ArrayList<BookingSlotDetail>();
		}
		bookingSlotDetails.add(bookingSlotDetail);
		return this;
	}
}
