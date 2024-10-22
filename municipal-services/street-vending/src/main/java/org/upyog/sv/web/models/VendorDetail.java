package org.upyog.sv.web.models;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.enums.VendorRelationshipType;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.common.AuditDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.micrometer.core.lang.NonNull;
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
	
	//Self referential vendor id
	private String vendorId;
	
	private String name;

	@NonNull
	@JsonFormat(pattern = StreetVendingUtil.DATE_FORMAT)
	private LocalDate dob;

	private Character gender;

	private String fatherName;

	private String mobileNo;

	private String emailId;
	
	//private VendorRelationshipType relationShipType;
	
	private String relationshipType;
	
	private AuditDetails auditDetails;
	
	
}
