package org.egov.inbox.model.vehicle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request schema of VehicleLog.  
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-12-23T12:08:13.326Z[GMT]")

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class VehicleTripDetail {


	  @JsonProperty("id")
	  private String id = null;

	  @NotNull
	  @NotBlank
	  @JsonProperty("tenantId")
	  private String tenantId = null;
	  
	  @NotNull
	  @NotBlank
	  @JsonProperty("referenceNo")
	  private String referenceNo = null;
	  
	  @NotBlank
	  @NotNull
	  @JsonProperty("referenceStatus")
	  private String referenceStatus = null;
	  

	    @JsonProperty("additionalDetails")
	    private Object additionalDetails = null;
	    
	  /**
	   * Gets or Sets status
	   */
	  public enum StatusEnum {
	    ACTIVE("ACTIVE"),
	    
	    INACTIVE("INACTIVE");

	    private String value;

	    StatusEnum(String value) {
	      this.value = value;
	    }

	    @Override
	    @JsonValue
	    public String toString() {
	      return String.valueOf(value);
	    }

	    @JsonCreator
	    public static StatusEnum fromValue(String text) {
	      for (StatusEnum b : StatusEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }
	  }
	  @JsonProperty("status")
	  private StatusEnum status = null;

	  @JsonProperty("itemStartTime")
	  private Long itemStartTime = null;
	  
	  @JsonProperty("itemEndTime")
	  private Long itemEndTime = null;
	  
	  @JsonProperty("volume")
	  private Double volume = null;
	  
	  @JsonProperty("auditDetails")
	  private AuditDetails auditDetails = null;

	@JsonProperty("trip_id")
	private String trip_id = null;
	  
}
