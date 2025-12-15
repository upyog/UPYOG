package org.upyog.chb.web.models;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.upyog.chb.web.models.billing.Demand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */
@Schema(description = "A Object holds the community halls slot avaialabiltiy details")
@Valid
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallDemandEstimationResponse   {
	
	private ResponseInfo responseInfo;
	
	private List<Demand> demands; 


}

