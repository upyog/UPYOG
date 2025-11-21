package org.egov.asset.web.models.disposal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AssetDisposalSearchCriteria is a class that holds the search filters for asset disposal queries.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisposalSearchCriteria {

    private String tenantId; // Tenant ID for multi-tenancy

    private List<String> applicationNos; // List of application numbers to filter

    private List<String> disposalIds; // List of asset IDs to filter

    private List<String> assetIds; // List of asset IDs to filter

    private Long fromDate; // Start date for the disposal date range (epoch milliseconds)

    private Long toDate; // End date for the disposal date range (epoch milliseconds)

    private String reasonForDisposal; // Disposal reasons to filter (comma-separated values)

    private Integer limit; // Limit for pagination

    private Integer offset; // Offset for pagination
}
