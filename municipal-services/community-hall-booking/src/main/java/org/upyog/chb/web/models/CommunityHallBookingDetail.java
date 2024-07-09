package org.upyog.chb.web.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	private String bookingId;
	
	private String bookingNo;
	
	private Long paymentDate;
	
	private Long applicationDate;
	
	@NotBlank
	private String tenantId;
	
	@NotBlank
	private String communityHallCode;
	
	@JsonProperty("bookingStatus")
	private String bookingStatus;

	@Valid
	@NotNull
	private SpecialCategory specialCategory;

	@Valid
	@NotNull
	private BookingPurpose purpose;
	
	@NotBlank
	private String purposeDescription;

	@NotNull
	@Valid
	private List<BookingSlotDetail> bookingSlotDetails;
	
	@JsonProperty("documents")
	@Valid
	private List<DocumentDetail> uploadedDocumentDetails;
	
	@Valid
	private ApplicantDetail applicantDetail;
	
	@Valid 
	private Address address;
	
	private AuditDetails auditDetails;
	
 	private ProcessInstance workflow;
	

	public CommunityHallBookingDetail addUploadedDocumentDetailsItem(DocumentDetail uploadedDocumentDetailsItem) {
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
