package org.upyog.rs.web.models.waterTanker;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.validator.CreateApplicationGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.upyog.rs.web.models.Address;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.AuditDetails;
import org.upyog.rs.web.models.Workflow;

/**
 * Details for new booking of advertisement
 */
@ApiModel(description = "Details for new booking of Water Tanker")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WaterTankerBookingDetail {

	@JsonProperty("bookingId")
	private String bookingId;

	private String bookingNo;

	@NotBlank
	private String tankerType;

	@NotBlank
	private String waterType;

	@NotNull
	private int tankerQuantity;

	@NotNull
	private int waterQuantity;

	@NotBlank
	private String description;

	
	private String applicantUuid;

	@JsonFormat(pattern = RequestServiceUtil.DATE_FORMAT)
	@NotNull
	private LocalDate deliveryDate;

	@JsonFormat(pattern = "HH:mm")
	@NotNull
	private LocalTime deliveryTime;

	private String extraCharge;

	private String vendorId;

	private String vehicleId;

	private String driverId;

	private String vehicleType;

	private String vehicleCapacity;

	private Long paymentDate;

	private Long applicationDate;
	
	private String bookingCreatedBy;  // Created by Citizen or Employee

	@NotBlank(groups = CreateApplicationGroup.class)
	private String tenantId;

	private String addressDetailId;

	@JsonProperty("bookingStatus")
	@NotBlank
	private String bookingStatus;

	private String receiptNo;

	private String permissionLetterFilestoreId;

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

	private AuditDetails auditDetails;

}
