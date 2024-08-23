package digit.web.models;

import java.util.List;

import org.springframework.stereotype.Component;

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
@Component
public class SchemeApplicationSearchCriteria {

    private String uuid;
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
    
    @JsonProperty("schemeId")
    private Long schemeId;
    @JsonProperty("machineId")
    private Long machineId = null;
    @JsonProperty("courseId")
    private Long courseId;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("number")
    private Long randomizationNumber;

    private String previousState = null;
    
}
