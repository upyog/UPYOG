package org.egov.demand.web.contract;

import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.response.ResponseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Compact API response envelope wrapping the search results from the
 * `/bill/v2/short/_search` endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortBillResponseV2 {

    /**
     * Standard response metadata envelope.
     */
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    /**
     * List of matching compact bill summaries.
     */
    @JsonProperty("Bill")
    private List<ShortBillV2> bill = new ArrayList<>();
}
