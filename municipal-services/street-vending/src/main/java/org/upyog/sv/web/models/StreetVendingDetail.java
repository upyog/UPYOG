package org.upyog.sv.web.models;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.enums.StreetVendingApplicationStatus;
import org.upyog.sv.web.models.common.AuditDetails;

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

	private String tenantId;

	private String applicationNo;

	private Long applicationDate;

	private String certificateNo;

	private Long approvalDate;

	private String applicationStatus;
	//StreetVendingApplicationStatus

	private String tradeLicenseNo;

	private String vendingActivity;

	private String vendingZone;

	private BigDecimal cartLatitude;
	private BigDecimal cartLongitude;

	private Integer vendingArea;

	private String localAuthorityName;
//Store file id of vending certificate 
	private String vendingLicenseCertificateId;

	private String disabilityStatus;

	private String benificiaryOfSocialSchemes;

	private String termsAndCondition;

	private List<Address> addressDetails;

	private BankDetail bankDetail;

	private List<DocumentDetail> documentDetails;

	private List<VendorDetail> vendorDetail;

	private List<VendingOperationTimeDetails> vendingOperationTimeDetails;

	private Workflow workflow;

	private AuditDetails auditDetails;

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
