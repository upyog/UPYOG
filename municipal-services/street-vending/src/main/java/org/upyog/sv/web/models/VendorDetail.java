package org.upyog.sv.web.models;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.validator.CreateApplicationGroup;
import org.upyog.sv.web.models.common.AuditDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class VendorDetail {

	private String id;

	private String applicationId;

	// Self referential vendor id
	private String vendorId;
	@NotBlank(groups = CreateApplicationGroup.class)
	private String name;

	@NotBlank(groups = CreateApplicationGroup.class)
	@JsonFormat(pattern = StreetVendingUtil.DATE_FORMAT)
	private String dob;

	@NotBlank(groups = CreateApplicationGroup.class)
	private Character gender;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String fatherName;

	private String mobileNo;

	private String emailId;

	// private VendorRelationshipType relationShipType;

	private String relationshipType;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String userCategory;

	private String specialCategory;

	private Boolean isInvolved;

	private AuditDetails auditDetails;

	public VendorDetail(VendorDetail vendorDetail) {
		this.id = vendorDetail.getId();
		this.applicationId = vendorDetail.getApplicationId();
		this.vendorId = vendorDetail.getVendorId();
		this.name = vendorDetail.getName();
		this.dob = vendorDetail.getDob();
		this.gender = vendorDetail.getGender();
		this.fatherName = vendorDetail.getFatherName();
		this.mobileNo = vendorDetail.getMobileNo();
		this.emailId = vendorDetail.getEmailId();
		this.relationshipType = vendorDetail.getRelationshipType();
		this.userCategory = vendorDetail.getUserCategory();
		this.specialCategory = vendorDetail.getSpecialCategory();
		this.isInvolved = vendorDetail.getIsInvolved();
		this.auditDetails = vendorDetail.getAuditDetails();
	}

}
