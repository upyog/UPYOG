package upyog.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllotmentSearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("AllotmentSearchCriteria")
    private AllotmentSearchCriteria criteria;
}
