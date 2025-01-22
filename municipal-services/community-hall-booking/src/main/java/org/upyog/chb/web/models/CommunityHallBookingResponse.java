package org.upyog.chb.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */
@ApiModel(description = "A Object holds the community halls for booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallBookingResponse   {
	
	private ResponseInfo responseInfo;
	
	@JsonProperty("hallsBookingApplication")
	@Valid
	private List<CommunityHallBookingDetail> hallsBookingApplication; 
	
	private List<Asset> assets; 

	
    @JsonProperty("applicationsRenewed")
    private int applicationsRenewed;
    
    @JsonProperty("applicationValidity")
    private int validity;
    
    @JsonProperty("applicationInitiated")
    private int applicationInitiated;
    
    @JsonProperty("applicationApplied")
    private int applicationApplied;
    
    @JsonProperty("applicationPendingForPayment")
    private int applicationPendingForPayment;
    
    @JsonProperty("applicationApproved")
    private int applicationApproved;
	
	public void addNewHallsBookingApplication(CommunityHallBookingDetail bookingDetail) {
		if(this.hallsBookingApplication == null) {
			this.hallsBookingApplication = new ArrayList<CommunityHallBookingDetail>();
		}
		this.hallsBookingApplication.add(bookingDetail);
	}
	
	private Integer count;

}

