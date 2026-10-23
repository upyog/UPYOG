package org.upyog.sv.web.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.common.AuditDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
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
public class VendingOperationTimeDetails {
	
	private String id;
	
	private String applicationId;
	
	private DayOfWeek dayOfWeek;
	
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime fromTime;
	
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime  toTime;
	
	private AuditDetails auditDetails;
	
}
