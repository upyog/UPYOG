package org.upyog.sv.web.models;

import java.time.LocalDate;

import org.upyog.sv.web.models.common.AuditDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class VendorPaymentSchedule {

	private String id;
	
	private String vendorId;
	
	private String certificateNo;
	
	private String paymentReceiptNo;
	
	private String applicationNo;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dueDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate lastPaymentDate;
	
	private PaymentScheduleStatus status;
	
	private AuditDetails auditDetails;

}
