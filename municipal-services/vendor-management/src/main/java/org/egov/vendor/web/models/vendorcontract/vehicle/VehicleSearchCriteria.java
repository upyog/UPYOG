package org.egov.vendor.web.models.vendorcontract.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleSearchCriteria {
	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("ownerId")
	private List<String> ownerId;
	
	@JsonProperty("ids")
	private List<String> ids;
	
	@JsonProperty("registrationNumber")
	private List<String> registrationNumber;
	
	@JsonProperty("type")
	private List<String> type;
	
	@JsonProperty("model")
	private List<String> model;
	
	@JsonProperty("tankCapacity")
	private Double tankCapacity;
	
	@JsonProperty("suctionType")
	private List<String> suctionType;

	@JsonProperty("vehicleType")
	private String vehicleType;

	@JsonProperty("vehicleCapacity")
	private String vehicleCapacity;

	@JsonProperty("sortBy")
	private SortBy sortBy;
	    
	@JsonProperty("sortOrder")
	private SortOrder sortOrder;
	
	@JsonProperty("status")
	private List<String> status;

	    
	public enum SortOrder {
	        ASC,
	        DESC
	}

    public enum SortBy {
	        type,
	        model,
	        suctionType,
	        pollutionCertiValidTill,
	        InsuranceCertValidTill,
	        fitnessValidTill,
	        roadTaxPaidTill,
	        tankCapicity,
	        createdTime
    }
	
}
