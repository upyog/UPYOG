package digit.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Collection of search criteria fields used for SchemeApplication searches.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeApplicationSearchCriteria {
    @JsonProperty("tenantId")
    private String tenantId; 
    @JsonProperty("ids")
    private List<String> ids; 
    @JsonProperty("applicationStatus")
    private Boolean applicationStatus; 
    @JsonProperty("verificationStatus")
    private Boolean verificationStatus; 
    @JsonProperty("firstApprovalStatus")
    private Boolean firstApprovalStatus; 
    @JsonProperty("finalApproval")
    private Boolean finalApproval; 
    @JsonProperty("randomSelection")
    private Boolean randomSelection; 
    @JsonProperty("submitted")
    private Boolean submitted; 
    @JsonProperty("applicationNumber")
    private String applicationNumber; 
    @JsonProperty("userId")
    private Long userId; 
    @JsonProperty("startDate")
    private Long startDate; 

    @JsonProperty("endDate")
    private Long endDate; 
    
}
