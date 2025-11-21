package org.egov.asset.web.models.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AssetMaintenanceSearchCriteria is a class that holds the search filters for asset maintenance queries.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMaintenanceSearchCriteria {

    private String tenantId; // Tenant ID for multi-tenancy

    private List<String> maintenanceIds;

    private List<String> assetIds; // List of asset IDs to filter

    private String maintenanceType; // Maintenance type to filter (e.g., Preventive, Corrective)

    private String vendor; // Vendor name to filter

    private Long fromDate; // Start date for the maintenance date range (epoch milliseconds)

    private Long toDate; // End date for the maintenance date range (epoch milliseconds)

    private String category; // Asset category to filter (e.g., Machinery, Furniture)

    private String warrantyStatus; // Warranty status to filter (e.g., IN_WARRANTY, IN_AMC, NA)

    private Integer limit; // Limit for pagination

    private Integer offset; // Offset for pagination
}
