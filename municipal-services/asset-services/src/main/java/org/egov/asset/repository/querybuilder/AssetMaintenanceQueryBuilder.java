package org.egov.asset.repository.querybuilder;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AssetMaintenanceQueryBuilder {

    @Autowired
    private AssetConfiguration config;

    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String BASE_QUERY =
            "SELECT maintenance.maintenance_id, "
                    + "maintenance.asset_id, "
                    + "maintenance.tenant_id, "
                    + "maintenance.current_life_of_asset, "
                    + "maintenance.is_warranty_expired, "
                    + "maintenance.is_amc_expired, "
                    + "maintenance.warranty_status, "
                    + "maintenance.amc_details, "
                    + "maintenance.maintenance_type, "
                    + "maintenance.payment_type, "
                    + "maintenance.cost_of_maintenance, "
                    + "maintenance.vendor, "
                    + "maintenance.maintenance_cycle, "
                    + "maintenance.parts_added_or_replaced, "
                    + "maintenance.additional_details, "
                    + "maintenance.created_by, "
                    + "maintenance.created_time, "
                    + "maintenance.last_modified_by, "
                    + "maintenance.last_modified_time, "
                    + "maintenance.post_condition_remarks, "
                    + "maintenance.pre_condition_remarks, "
                    + "maintenance.description, "
                    + "maintenance.asset_maintenance_status, "
                    + "maintenance.asset_maintenance_date,"
                    + "maintenance.asset_next_maintenance_date,"
                    + "doc.documentid, "
                    + "doc.documenttype, "
                    + "doc.filestoreid, "
                    + "doc.documentuid, "
                    + "doc.docdetails "
                    + "FROM eg_asset_maintenance maintenance "
                    + LEFT_OUTER_JOIN_STRING + " eg_asset_maintenance_documents doc ON maintenance.maintenance_id = doc.maintenanceid ";
                    //+ " AND doc.documenttype IN ('ASSET.MAINTENANCE.DOC1', 'ASSET.MAINTENANCE.DOC2', 'ASSET.MAINTENANCE.DOC3')";

    private final String paginationWrapper = "SELECT * FROM " +
            "(SELECT result.*, DENSE_RANK() OVER (ORDER BY result.created_time DESC) AS offset_ FROM " +
            "({}) result) result_offset WHERE offset_ > ? AND offset_ <= ?";

    /**
     * Builds the SQL query for searching asset maintenance records based on the given criteria.
     *
     * @param criteria         Maintenance search criteria
     * @param preparedStmtList Values to be replaced in the query
     * @return The final SQL query
     */
    public String getMaintenanceSearchQuery(AssetMaintenanceSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(BASE_QUERY);

        // Add tenant ID filter
        if (criteria.getTenantId() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.tenant_id = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        // Add maintenance IDs filter
        if (!CollectionUtils.isEmpty(criteria.getMaintenanceIds())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.maintenance_id IN (")
                    .append(createQuery(criteria.getMaintenanceIds()))
                    .append(") ");
            addToPreparedStatement(preparedStmtList, criteria.getMaintenanceIds());
        }

        // Add asset IDs filter
        if (!CollectionUtils.isEmpty(criteria.getAssetIds())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.asset_id IN (")
                    .append(createQuery(criteria.getAssetIds()))
                    .append(") ");
            addToPreparedStatement(preparedStmtList, criteria.getAssetIds());
        }

        // Add date range filter
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.created_time BETWEEN ? AND ? ");
            preparedStmtList.add(criteria.getFromDate());
            preparedStmtList.add(criteria.getToDate());
        } else if (criteria.getFromDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.created_time >= ? ");
            preparedStmtList.add(criteria.getFromDate());
        } else if (criteria.getToDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" maintenance.created_time <= ? ");
            preparedStmtList.add(criteria.getToDate());
        }

        // Add pagination
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
    }

    /**
     * Adds WHERE or AND clause to the query if required.
     *
     * @param values       The prepared statement values
     * @param queryBuilder The query builder
     */
    private void addClauseIfRequired(List<Object> values, StringBuilder queryBuilder) {
        if (values.isEmpty()) {
            queryBuilder.append(" WHERE ");
        } else {
            queryBuilder.append(" AND ");
        }
    }

    /**
     * Adds values to the prepared statement list.
     *
     * @param preparedStmtList The prepared statement list
     * @param values            The values to add
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> values) {
        values.forEach(preparedStmtList::add);
    }

    /**
     * Creates a placeholder query for the IN clause.
     *
     * @param values The values for the IN clause
     * @return The query with placeholders
     */
    private String createQuery(List<String> values) {
        return String.join(", ", values.stream().map(v -> "?").toArray(String[]::new));
    }

    /**
     * Adds pagination to the query.
     *
     * @param query            The base query
     * @param preparedStmtList The prepared statement values
     * @param criteria         The search criteria
     * @return The paginated query
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, AssetMaintenanceSearchCriteria criteria) {
        int limit = config.getDefaultLimit();
        int offset = config.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if (criteria.getLimit() == null && criteria.getOffset() == null) {
            limit = config.getMaxSearchLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        } else if (criteria.getLimit() != null) {
            limit = config.getMaxSearchLimit();
        }

        if (criteria.getOffset() != null) {
            offset = criteria.getOffset();
        }

        if (limit == -1) {
            finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
        } else {
            preparedStmtList.add(offset);
            preparedStmtList.add(limit + offset);
        }

        return finalQuery;
    }
}
