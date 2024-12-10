package org.upyog.adv.web.models;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of advertisement
 */
@ApiModel(description = "Request object for creating new booking of Advertisemnets")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlotSearchRequest {
	
	@Valid
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

	@Valid
	@JsonProperty("advertisementSlotSearchCriteria")
	private  AdvertisementSlotSearchCriteria criteria; 
	

	
}
