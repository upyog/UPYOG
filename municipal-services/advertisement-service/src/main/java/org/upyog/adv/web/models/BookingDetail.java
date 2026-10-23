package org.upyog.adv.web.models;

import java.util.ArrayList;
import java.util.List;

import org.upyog.adv.validator.CreateApplicationGroup;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details for new booking of advertisement
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookingDetail {

	@JsonProperty("bookingId")
	private String bookingId;

	private String bookingNo;

	private Long paymentDate;

	private String draftId;

	private Long applicationDate;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String tenantId;

	@JsonProperty("bookingStatus")
	private String bookingStatus;

	private String receiptNo;

	private String permissionLetterFilestoreId;

	private String paymentReceiptFilestoreId;

	@NotNull(groups = CreateApplicationGroup.class)
	@Valid
	@JsonProperty("cartDetails")
	@JsonAlias("CartDetails")
	private List<CartDetail> cartDetails;

	@JsonProperty("documents")
	@Valid
	private List<DocumentDetail> uploadedDocumentDetails;

	@Valid
	private ApplicantDetail applicantDetail;

	@Valid
	private Address address;

	private AuditDetails auditDetails;

	public BookingDetail addUploadedDocumentDetailsItem(DocumentDetail uploadedDocumentDetailsItem) {
		if (this.uploadedDocumentDetails == null) {
			this.uploadedDocumentDetails = new ArrayList<>();
		}
		this.uploadedDocumentDetails.add(uploadedDocumentDetailsItem);
		return this;
	}

	public BookingDetail addBookingSlots(CartDetail cartDetail) {
		if (cartDetails == null) {
			cartDetails = new ArrayList<>();
		}
		cartDetails.add(cartDetail);
		return this;
	}
}
