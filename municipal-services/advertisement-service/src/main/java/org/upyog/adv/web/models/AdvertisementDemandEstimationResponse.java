package org.upyog.adv.web.models;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.adv.web.models.billing.Demand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementDemandEstimationResponse   {
	
	private ResponseInfo responseInfo;
	
	private List<Demand> demands; 


}

