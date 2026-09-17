package org.upyog.draft.repository.query;

import org.springframework.stereotype.Component;
import org.upyog.draft.web.models.DraftSearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Component
public class DraftQueryBuilder {

    private static final String BASE_SELECT = "SELECT draft_id, tenant_id, business_service, module_name, "
            + "module_entity_id, creator_type, draft_data, completion_pct, status, createdby, lastmodifiedby, "
            + "createdtime, lastmodifiedtime FROM ug_draft_detail WHERE 1=1";

    public String getSearchQuery(DraftSearchCriteria criteria, List<Object> params) {
        StringBuilder query = new StringBuilder(BASE_SELECT);
        appendCriteria(query, criteria, params);
        appendSort(query, criteria);
        appendPagination(query, criteria, params);
        return query.toString();
    }

    public String getCountQuery(DraftSearchCriteria criteria, List<Object> params) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM ug_draft_detail WHERE 1=1");
        appendCriteria(query, criteria, params);
        return query.toString();
    }

    private void appendCriteria(StringBuilder query, DraftSearchCriteria criteria, List<Object> params) {
        if (criteria.getTenantId() != null) {
            query.append(" AND tenant_id = ?");
            params.add(criteria.getTenantId());
        }
        if (criteria.getCreatedBy() != null) {
            query.append(" AND createdby = ?");
            params.add(criteria.getCreatedBy());
        }
        if (criteria.getBusinessService() != null) {
            query.append(" AND business_service = ?");
            params.add(criteria.getBusinessService());
        }
        if (criteria.getModuleName() != null) {
            query.append(" AND module_name = ?");
            params.add(criteria.getModuleName());
        }
        if (criteria.getCreatorType() != null) {
            query.append(" AND creator_type = ?");
            params.add(criteria.getCreatorType());
        }
        if (criteria.getStatus() != null) {
            query.append(" AND status = ?");
            params.add(criteria.getStatus());
        }
    }

    private void appendSort(StringBuilder query, DraftSearchCriteria criteria) {
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "lastmodifiedtime";
        if ("lastModifiedTime".equalsIgnoreCase(sortBy)) {
            sortBy = "lastmodifiedtime";
        }
        String sortOrder = "ASC".equalsIgnoreCase(criteria.getSortOrder()) ? "ASC" : "DESC";
        query.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
    }

    private void appendPagination(StringBuilder query, DraftSearchCriteria criteria, List<Object> params) {
        int offset = criteria.getOffset() != null ? criteria.getOffset() : 0;
        int limit = criteria.getLimit() != null ? criteria.getLimit() : 10;
        query.append(" OFFSET ? LIMIT ?");
        params.add(offset);
        params.add(limit);
    }

    public List<Object> activeTtlParams(long cutoffTime) {
        List<Object> params = new ArrayList<>();
        params.add(cutoffTime);
        return params;
    }
}
