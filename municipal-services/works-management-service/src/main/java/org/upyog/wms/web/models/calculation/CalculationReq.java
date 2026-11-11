package org.upyog.wms.web.models.calculation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationReq {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("CalculationCriteria")
    private List<CalculationCriteria> calculationCriteria;
}
