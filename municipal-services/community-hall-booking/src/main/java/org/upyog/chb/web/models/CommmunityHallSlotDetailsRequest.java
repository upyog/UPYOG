package org.upyog.chb.web.models;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommmunityHallSlotDetailsRequest {
	@NonNull
	@Valid
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@NotBlank
	private String communityHallCode;

}
