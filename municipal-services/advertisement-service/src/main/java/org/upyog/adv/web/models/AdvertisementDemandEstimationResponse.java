package org.upyog.adv.web.models;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.adv.web.models.billing.Demand;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */
@ApiModel(description = "A Object holds the community halls slot avaialabiltiy details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementDemandEstimationResponse   {
	
	private ResponseInfo responseInfo;
	
	private List<Demand> demands; 


}

