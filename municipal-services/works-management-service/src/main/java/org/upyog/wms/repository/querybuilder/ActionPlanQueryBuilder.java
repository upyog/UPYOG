package org.upyog.wms.repository.querybuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;

/**
 * Builds parameterised SQL. Every value goes through a '?' placeholder (never concatenated);
 * only whitelisted column names are placed in ORDER BY.
 */
@Component
@RequiredArgsConstructor
public class ActionPlanQueryBuilder {

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "createdTime", "ap.createdtime",
            "approvedDate", "ap.approveddate",
            "projectId", "ap.projectid",
            "applicationNumber", "ap.applicationnumber",
            "actionPlanNumber", "ap.actionplannumber");

    private static final String BASE_QUERY = """
            SELECT ap.*, COUNT(*) OVER() AS full_count
            FROM eg_wms_action_plan ap
            WHERE ap.isactive = true
            """;

    private static final String COUNT_QUERY = """
            SELECT COUNT(*)
            FROM eg_wms_action_plan ap
            WHERE ap.isactive = true
            """;

    private final WmsConfiguration config;

    /** Page of headers (with total count in every row). */
    public String getSearchQuery(ActionPlanSearchCriteria criteria, List<Object> params) {
        StringBuilder sql = new StringBuilder(BASE_QUERY);
        addFilters(criteria, sql, params);

        String column = SORT_COLUMNS.getOrDefault(criteria.getSortBy(), "ap.createdtime");
        String order = "ASC".equalsIgnoreCase(criteria.getSortOrder()) ? "ASC" : "DESC";
        sql.append(" ORDER BY ").append(column).append(' ').append(order).append(" NULLS LAST, ap.id");

        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit(criteria));
        params.add(criteria.getOffset() == null ? 0 : criteria.getOffset());
        return sql.toString();
    }

    public String getCountQuery(ActionPlanSearchCriteria criteria, List<Object> params) {
        StringBuilder sql = new StringBuilder(COUNT_QUERY);
        addFilters(criteria, sql, params);
        return sql.toString();
    }

    public int limit(ActionPlanSearchCriteria criteria) {
        int limit = criteria.getLimit() == null ? config.getDefaultLimit() : criteria.getLimit();
        return Math.min(limit, config.getMaxLimit());
    }

    /** Child rows of the given plans. Table names are constants of this class, never user input. */
    public String getChildQuery(String table, String parentColumn, Collection<String> parentIds,
                                String orderBy, List<Object> params) {
        params.addAll(parentIds);
        return "SELECT * FROM " + table + " WHERE " + parentColumn + " IN (" + placeholders(parentIds.size())
                + ") AND isactive = true ORDER BY " + orderBy;
    }

    private void addFilters(ActionPlanSearchCriteria c, StringBuilder sql, List<Object> params) {
        if (c.getTenantId().contains(".")) {
            sql.append(" AND ap.tenantid = ?");
            params.add(c.getTenantId());
        } else {
            sql.append(" AND (ap.tenantid = ? OR ap.tenantid LIKE ?)");
            params.add(c.getTenantId());
            params.add(c.getTenantId() + ".%");
        }
        in(sql, params, "ap.id", c.getIds());
        in(sql, params, "ap.applicationnumber", c.getApplicationNumbers());
        in(sql, params, "ap.actionplannumber", c.getActionPlanNumbers());
        in(sql, params, "ap.projectid", c.getProjectIds());
        in(sql, params, "ap.status", c.getStatuses());
        eq(sql, params, "ap.financialyear", c.getFinancialYear());
        eq(sql, params, "ap.schemecode", c.getSchemeCode());
        eq(sql, params, "ap.fundsource", c.getFundSource());
        eq(sql, params, "ap.createdby", c.getCreatedBy());
        if (c.getFromDate() != null) {
            sql.append(" AND ap.createdtime >= ?");
            params.add(c.getFromDate());
        }
        if (c.getToDate() != null) {
            sql.append(" AND ap.createdtime <= ?");
            params.add(c.getToDate());
        }
        if (Boolean.TRUE.equals(c.getLatestVersionOnly())) {
            sql.append(" AND ap.islatestversion = true");
        }
        if (c.getDistrictCode() != null) {
            sql.append("""
                     AND EXISTS (SELECT 1 FROM eg_wms_ap_location l
                                 WHERE l.actionplanid = ap.id AND l.isactive = true AND l.districtcode = ?)""");
            params.add(c.getDistrictCode());
        }
    }

    private static void eq(StringBuilder sql, List<Object> params, String column, Object value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" = ?");
            params.add(value);
        }
    }

    private static void in(StringBuilder sql, List<Object> params, String column, List<String> values) {
        if (values != null && !values.isEmpty()) {
            sql.append(" AND ").append(column).append(" IN (").append(placeholders(values.size())).append(')');
            params.addAll(values);
        }
    }

    private static String placeholders(int count) {
        return java.util.stream.Stream.generate(() -> "?").limit(count).collect(Collectors.joining(", "));
    }
}
