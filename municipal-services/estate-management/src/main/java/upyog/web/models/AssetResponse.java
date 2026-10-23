package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Assets")
    private List<Asset> assets;
}