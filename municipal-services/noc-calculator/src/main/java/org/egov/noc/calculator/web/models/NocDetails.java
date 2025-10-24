package org.egov.noc.calculator.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

/**
 * Represents detailed information related to a NOC application.
 */
@ApiModel(description = "Represents detailed information related to a NOC application.")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NocDetails {

    @JsonProperty("id")
    @ApiModelProperty(value = "Unique identifier for NOC details")
    private String id;

    @JsonProperty("nocId")
    @ApiModelProperty(value = "Reference to the NOC application ID")
    private String nocId;

    @JsonProperty("additionalDetails")
    @ApiModelProperty(value = "Additional details provided for the NOC")
    private Object additionalDetails;

    @JsonProperty("tenantId")
    @ApiModelProperty(value = "Tenant ID associated with the NOC details")
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNocId() {
        return nocId;
    }

    public void setNocId(String nocId) {
        this.nocId = nocId;
    }

    public Object getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(Object additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }
    public String getTenantId(String tenantId){
       return tenantId;
    }






}



