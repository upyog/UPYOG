package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("Assets")
    private List<Asset> assets;
}