package org.upyog.sv.web.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.enums.ApplicationCreatedByEnum;
import org.upyog.sv.validator.CreateApplicationGroup;
import org.upyog.sv.web.models.common.AuditDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class StreetVendingDetail {

	private String applicationId;

	private String draftId;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String tenantId;

	private String applicationNo;

	private Long applicationDate;

	private String certificateNo;

	private Long approvalDate;

	private String applicationStatus;

	private String tradeLicenseNo;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String vendingActivity;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String vendingZone;
	
	private String vendingZoneValue;

	private BigDecimal cartLatitude;

	private BigDecimal cartLongitude;

	@NotNull(groups = CreateApplicationGroup.class)
	private BigDecimal vendingArea;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String localAuthorityName;
//Store file id of vending certificate 

	private String vendingLicenseCertificateId;

	private String paymentReceiptId;

	private String vendingLicenseId;

	private String disabilityStatus;
	
	private String locality;
	
	private String localityValue;
	
	private ApplicationCreatedByEnum applicationCreatedBy;

	private List<BeneficiaryScheme> benificiaryOfSocialSchemes;

	private String termsAndCondition;

	// Below fields added for certificate validity
	@JsonProperty("issuedDate")
	private Long issuedDate = null;

	@JsonProperty("financialYear")
	private String financialYear = null;

	@JsonProperty("validFrom")
	private String validFrom = null;

	@JsonProperty("validTo")
	private String validTo = null;

	@Valid
	@NotNull(groups = CreateApplicationGroup.class)
	private List<@Valid Address> addressDetails;

	private BankDetail bankDetail;

	private List<DocumentDetail> documentDetails;

	private List<VendorDetail> vendorDetail;

	private List<VendingOperationTimeDetails> vendingOperationTimeDetails;

	private Workflow workflow;

	private AuditDetails auditDetails;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate validityDate;

	private String validityDateForPersisterDate;

//	private Boolean eligibleToRenew;

	private Boolean expireFlag;

	private String oldApplicationNo;

	private RenewalStatus renewalStatus;
	
	private String vendorPaymentFrequency;
	
	private String formattedApprovalDate;
	
	private String formattedApplicationDate;


}

/**
 * CREATE TABLE vendors ( id PRIMARY KEY, -- Unique identifier for each
 * vendor/dependent name VARCHAR(100) NOT NULL, -- Name of the vendor or
 * dependent email VARCHAR(100) UNIQUE, -- Email of the vendor (use NULL for
 * dependents) phone_number VARCHAR(15), -- Phone number of the vendor (use NULL
 * for dependents) relationship VARCHAR(50), -- Relationship to the vendor (NULL
 * for vendors) vendor_id INT, -- To link dependents to their vendor FOREIGN KEY
 * (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE );
 * 
 */
