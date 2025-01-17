package org.upyog.request.service.web.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.upyog.request.service.util.RequestServiceUtil;
import org.upyog.request.service.validator.CreateApplicationGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
	
	private String tankerType;

    private String tankerQuantity;

    private String waterQuantity;

    private String description;

    @JsonFormat(pattern = RequestServiceUtil.DATE_FORMAT)
    private LocalDate deliveryDate;
  
    @JsonFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
  
    private String extraCharge;
 
    private String vendorId;

    private String vehicleId;

    private String driverId;
	
	private Long paymentDate;

	private Long applicationDate;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	private String tenantId;
	
	@JsonProperty("bookingStatus")
	private String bookingStatus;
	
	private String receiptNo;
	
	private String permissionLetterFilestoreId;
	
	private String paymentReceiptFilestoreId;
	
	@Valid
	private ApplicantDetail applicantDetail;
	
	@Valid 
	private Address address;
	
	private AuditDetails auditDetails;
	
}
