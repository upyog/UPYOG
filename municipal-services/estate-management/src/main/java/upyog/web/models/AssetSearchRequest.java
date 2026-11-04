package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.request.RequestInfo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetSearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("AssetSearchCriteria")
    private AssetSearchCriteria criteria;

}
