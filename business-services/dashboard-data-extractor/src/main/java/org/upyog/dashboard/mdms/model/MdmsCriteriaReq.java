package org.upyog.dashboard.mdms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.upyog.dashboard.model.RequestInfo;

/**
 * Request payload for MDMS search API.
 */
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
