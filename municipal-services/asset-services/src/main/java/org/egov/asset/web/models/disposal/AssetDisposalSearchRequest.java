package org.egov.asset.web.models.disposal;

import com.fasterxml.jackson.annotation.JsonProperty;
import digit.models.coremodels.RequestInfoWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisposalSearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfoWrapper requestInfoWrapper = null;

    @JsonProperty("searchCriteria")
    @Valid
    private AssetDisposalSearchCriteria assetDisposalSearchCriteria = null;
}
