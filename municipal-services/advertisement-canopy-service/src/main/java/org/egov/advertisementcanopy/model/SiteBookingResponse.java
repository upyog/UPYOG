package org.egov.advertisementcanopy.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteBookingResponse {


	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("SiteBookings")
	private List<SiteBooking> siteBookings;

    @JsonProperty("Count")
    private int count;
    
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

}
