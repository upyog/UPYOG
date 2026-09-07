package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllotmentSearchCriteria {
    
    //@JsonProperty("RequestInfo")
   // private RequestInfo requestInfo;
    
    private String assetNo;
    private String allotmentNo;
    private String alloteeName;
    private String status;
    private String tenantId;
    private String userUuid;
    private Integer limit;
    private Integer offset;
}