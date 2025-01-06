package org.egov.asset.repository.querybuilder;

import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AssetMaintenanceQueryBuilder {

    private static final String BASE_QUERY = "SELECT * FROM eg_asset_maintenance";

    public String getSearchQuery(AssetMaintenanceSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder queryBuilder = new StringBuilder(BASE_QUERY);
        boolean isWhereClauseAdded = false;

        if (StringUtils.hasText(criteria.getTenantId())) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" tenant_id = ?");
            preparedStmtList.add(criteria.getTenantId());
            isWhereClauseAdded = true;
        }

        if (criteria.getAssetIds() != null && !criteria.getAssetIds().isEmpty()) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" asset_id IN (")
                    .append(createPlaceholders(criteria.getAssetIds().size())).append(")");
            preparedStmtList.addAll(criteria.getAssetIds());
            isWhereClauseAdded = true;
        }

        if (StringUtils.hasText(criteria.getMaintenanceType())) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" maintenance_type = ?");
            preparedStmtList.add(criteria.getMaintenanceType());
            isWhereClauseAdded = true;
        }

        if (StringUtils.hasText(criteria.getWarrantyStatus())) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" warranty_status = ?");
            preparedStmtList.add(criteria.getWarrantyStatus());
            isWhereClauseAdded = true;
        }

        if (criteria.getFromDate() != null) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" maintenance_date >= ?");
            preparedStmtList.add(criteria.getFromDate());
            isWhereClauseAdded = true;
        }

        if (criteria.getToDate() != null) {
            queryBuilder.append(isWhereClauseAdded ? " AND" : " WHERE").append(" maintenance_date <= ?");
            preparedStmtList.add(criteria.getToDate());
            isWhereClauseAdded = true;
        }

        if (criteria.getLimit() != null && criteria.getOffset() != null) {
            queryBuilder.append(" LIMIT ? OFFSET ?");
            preparedStmtList.add(criteria.getLimit());
            preparedStmtList.add(criteria.getOffset());
        }

        return queryBuilder.toString();
    }

    private String createPlaceholders(int count) {
        return String.join(",", java.util.Collections.nCopies(count, "?"));
    }
}
