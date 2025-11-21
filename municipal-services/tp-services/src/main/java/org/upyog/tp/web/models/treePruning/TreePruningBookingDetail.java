package org.upyog.tp.web.models.treePruning;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.upyog.tp.web.models.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Details for new booking of Tree Pruning
 */
@Schema(description = "Details for new booking of Tree Pruning")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TreePruningBookingDetail {

	@JsonProperty("bookingId")
	private String bookingId;

	private String bookingNo;

	private String applicantUuid;

	@NotBlank
	private String reasonForPruning;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private Long paymentDate;

	private Long applicationDate;

	private String bookingCreatedBy;  // Created by Citizen or Employee

	@NotBlank
	private String tenantId;

	private String addressDetailId;

	@JsonProperty("bookingStatus")
	@NotBlank
	private String bookingStatus;

	private String receiptNo;

	private String paymentReceiptFilestoreId;

	private String mobileNumber;

	private String localityCode;

	@Valid
	@NotNull
	private ApplicantDetail applicantDetail;

	@Valid
	@NotNull
	private Address address;

	@Valid
	@NotNull
	private Workflow workflow;

	@Valid
	@NotNull
	private List<DocumentDetail> documentDetails;

	private AuditDetails auditDetails;

}
