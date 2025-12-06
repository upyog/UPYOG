package org.upyog.adv.web.models;

import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of advertisement
 */
<<<<<<< HEAD
@Schema(description = "Request object for creating new booking of Advertisemnets")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "Request object for creating new booking of Advertisemnets")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

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
	private  List<AdvertisementSlotSearchCriteria> criteria; 
	

	
}
