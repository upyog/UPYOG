package org.egov.asset.repository.querybuilder;

import org.egov.asset.web.models.AssetInventoryProcurementRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AssetInventoryProcurementQueryBuilder {

    private static final String INSERT_QUERY = "INSERT INTO eg_asset_inventory_procurement_request " +
            "(request_id, item, item_type, quantity, asset_application_number, tenant_id, status, " +
            "created_by, created_time, last_modified_by, last_modified_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE eg_asset_inventory_procurement_request SET " +
            "item = ?, item_type = ?, quantity = ?, asset_application_number = ?, status = ?, " +
            "last_modified_by = ?, last_modified_time = ? WHERE request_id = ?";

    private static final String BASE_SEARCH_QUERY = "SELECT request_id, item, item_type, quantity, " +
            "asset_application_number, tenant_id, status, created_by, created_time, " +
            "last_modified_by, last_modified_time FROM eg_asset_inventory_procurement_request";

    public String getInsertQuery() {
        return INSERT_QUERY;
    }

    public String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    public Object[] getInsertParams(AssetInventoryProcurementRequest request) {
        return new Object[]{
                request.getRequestId(),
                request.getItem(),
                request.getItemType(),
                request.getQuantity(),
                request.getAssetApplicationNumber(),
                request.getTenantId(),
                request.getStatus(),
                request.getAuditDetails().getCreatedBy(),
                request.getAuditDetails().getCreatedTime(),
                request.getAuditDetails().getLastModifiedBy(),
                request.getAuditDetails().getLastModifiedTime()
        };
    }

    public Object[] getUpdateParams(AssetInventoryProcurementRequest request) {
        return new Object[]{
                request.getItem(),
                request.getItemType(),
                request.getQuantity(),
                request.getAssetApplicationNumber(),
                request.getStatus(),
                request.getAuditDetails().getLastModifiedBy(),
                request.getAuditDetails().getLastModifiedTime(),
                request.getRequestId()
        };
    }

    public String getSearchQuery(AssetInventoryProcurementRequest searchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        boolean isFirstCondition = true;

        if (searchCriteria != null) {
            if (StringUtils.hasText(searchCriteria.getRequestId())) {
                addCondition(query, isFirstCondition);
                query.append(" request_id = ?");
                preparedStmtList.add(searchCriteria.getRequestId());
                isFirstCondition = false;
            }

            if (StringUtils.hasText(searchCriteria.getTenantId())) {
                addCondition(query, isFirstCondition);
                query.append(" tenant_id = ?");
                preparedStmtList.add(searchCriteria.getTenantId());
                isFirstCondition = false;
            }

            if (StringUtils.hasText(searchCriteria.getAssetApplicationNumber())) {
                addCondition(query, isFirstCondition);
                query.append(" asset_application_number = ?");
                preparedStmtList.add(searchCriteria.getAssetApplicationNumber());
                isFirstCondition = false;
            }

            if (StringUtils.hasText(searchCriteria.getStatus())) {
                addCondition(query, isFirstCondition);
                query.append(" status = ?");
                preparedStmtList.add(searchCriteria.getStatus());
                isFirstCondition = false;
            }
        }

        query.append(" ORDER BY created_time DESC");
        return query.toString();
    }

    private void addCondition(StringBuilder query, boolean isFirstCondition) {
        if (isFirstCondition) {
            query.append(" WHERE");
        } else {
            query.append(" AND");
        }
    }
}