package org.egov.asset.repository.querybuilder;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class AssetDisposalQueryBuilder {

    @Autowired
    private AssetConfiguration config;

    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String BASE_QUERY = "SELECT "
            + "disposal.disposal_id, "
            + "disposal.asset_id, "
            + "disposal.tenant_id, "
            + "disposal.life_of_asset, "
            + "disposal.current_age_of_asset, "
            + "disposal.is_asset_disposed_in_facility, "
            + "disposal.disposal_date, "
            + "disposal.reason_for_disposal, "
            + "disposal.amount_received, "
            + "disposal.purchaser_name, "
            + "disposal.payment_mode, "
            + "disposal.receipt_number, "
            + "disposal.comments, "
            + "disposal.gl_code, "
            + "disposal.created_at, "
            + "disposal.created_by, "
            + "disposal.updated_at, "
            + "disposal.updated_by, "
            + "disposal.asset_disposal_status, "
            + "disposal.additional_details, "
            + "doc.documentid, "
            + "doc.documenttype, "
            + "doc.filestoreid, "
            + "doc.documentuid, "
            + "doc.docdetails "
            + "FROM eg_asset_disposal_details disposal "
            + LEFT_OUTER_JOIN_STRING + " eg_asset_disposal_documents doc ON disposal.disposal_id = doc.disposalid ";
            //+ " AND doc.documenttype IN ('ASSET.DISPOSE.DOC1')";

    private final String paginationWrapper = "SELECT * FROM " +
            "(SELECT result.*, DENSE_RANK() OVER (ORDER BY result.created_at DESC) AS offset_ FROM " +
            "({}) result) result_offset WHERE offset_ > ? AND offset_ <= ?";

    /**
     * Builds the SQL query for searching disposal records based on the given criteria.
     *
     * @param criteria         Disposal search criteria
     * @param preparedStmtList Values to be replaced in the query
     * @return The final SQL query
     */
    public String getDisposalSearchQuery(AssetDisposalSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(BASE_QUERY);

        // Add tenant ID filter
        if (criteria.getTenantId() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.tenant_id = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        // Add maintenance IDs filter
        if (!CollectionUtils.isEmpty(criteria.getDisposalIds())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.disposal_id IN (")
                    .append(createQuery(criteria.getDisposalIds()))
                    .append(") ");
            addToPreparedStatement(preparedStmtList, criteria.getDisposalIds());
        }

        // Add asset IDs filter
        if (!CollectionUtils.isEmpty(criteria.getAssetIds())) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.asset_id IN (")
                    .append(createQuery(criteria.getAssetIds()))
                    .append(") ");
            addToPreparedStatement(preparedStmtList, criteria.getAssetIds());
        }


        // Add disposal date filter
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.disposal_date BETWEEN ? AND ? ");
            preparedStmtList.add(criteria.getFromDate());
            preparedStmtList.add(criteria.getToDate());
        } else if (criteria.getFromDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.disposal_date >= ? ");
            preparedStmtList.add(criteria.getFromDate());
        } else if (criteria.getToDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" disposal.disposal_date <= ? ");
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
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, AssetDisposalSearchCriteria criteria) {
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
