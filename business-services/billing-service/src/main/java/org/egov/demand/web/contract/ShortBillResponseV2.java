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
 * Lightweight API response envelope wrapping compact bill summary search results.
 * <p>
 * Rationale: Created specifically to serve Dashboard V2 requirements. The standard bill search
 * returns extensive nested data (full bill details, account head breakdowns, payer objects)
 * which is redundant for dashboard listing views. This response encapsulates only the core fields
 * needed by Dashboard V2 to reduce payload size and optimize response times.
 * </p>
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
