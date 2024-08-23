package digit.web.models.employee;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class ApplicationStatusRequest {
    
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    
    @JsonProperty("ApplicationStatus")
    private ApplicationStatus applicationStatus;


}
