package org.upyog.wms.web.models.mdms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdmsCriteriaReq {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("MdmsCriteria")
    private MdmsCriteria mdmsCriteria;
}
