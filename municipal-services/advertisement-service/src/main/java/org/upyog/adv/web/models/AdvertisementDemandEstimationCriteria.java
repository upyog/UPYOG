package org.upyog.adv.web.models;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdvertisementDemandEstimationCriteria {
	
	@Valid
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
	
	@JsonProperty("tenantId")
	private String tenantId;


	@NotNull                                             
	@Valid                                               
	private List<CartDetail> cartDetails;  

}
