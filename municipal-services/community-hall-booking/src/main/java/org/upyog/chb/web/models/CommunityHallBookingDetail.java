package org.upyog.chb.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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
	
	private String approvalNo;
	
	private Long approvalDate;
	
	@JsonProperty("tenantId")
	private String tenantId = null;
	
	@JsonProperty("communityHallId")
	private Integer communityHallId = null;
	
	@JsonProperty("bookingStatus")
	private String bookingStatus;

	@JsonProperty("residentType")
	private ResidentType residentType = null;

	@JsonProperty("specialCategory")
	private SpecialCategory specialCategory = null;

	@JsonProperty("purpose")
	private BookingPurpose purpose = null;
	
	private String purposeDescription;

	@JsonProperty("eventName")
	private String eventName = null;

	@JsonProperty("eventOrganisedBy")
	private String eventOrganisedBy = null;
	
	@JsonProperty("bookingSlotDetails")
	private List<BookingSlotDetail> bookingSlotDetails;
	
	@JsonProperty("uploadedDocumentDetails")
	@Valid
	private List<DocumentDetails> uploadedDocumentDetails = null;
	
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
