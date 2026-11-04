package org.egov.nationaldashboardingest.web.models;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;

/**
 * Wrapper object pairing a parsed {@link Data} row with its associated UPYOG {@link RequestInfo}.
 * <p>
 * Enables streaming batch processing to carry authentication credentials and user context
 * extracted from the Excel spreadsheet alongside individual row metric values.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class IngestRowData {

    /**
     * UPYOG {@link RequestInfo} metadata (auth token, user UUID, action, API ID) assigned to this row.
     */
    private RequestInfo requestInfo;

    /**
     * Functional metrics and location data (date, ULB, module, metrics map) for this row.
     */
    private Data data;

}
