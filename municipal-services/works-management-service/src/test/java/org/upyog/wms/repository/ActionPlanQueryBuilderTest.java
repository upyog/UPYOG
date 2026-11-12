package org.upyog.wms.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.repository.querybuilder.ActionPlanQueryBuilder;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;

class ActionPlanQueryBuilderTest {

    private static final class Config extends WmsConfiguration {
        @Override
        public Integer getDefaultLimit() {
            return 10;
        }

        @Override
        public Integer getMaxLimit() {
            return 100;
        }
    }

    private final ActionPlanQueryBuilder builder = new ActionPlanQueryBuilder(new Config());

    private static long placeholders(String sql) {
        return sql.chars().filter(c -> c == '?').count();
    }

    @Test
    void everyValueIsABindParameter() {
        ActionPlanSearchCriteria criteria = ActionPlanSearchCriteria.builder()
                .tenantId("od.cuttack").ids(List.of("a", "b")).applicationNumbers(List.of("X"))
                .actionPlanNumbers(List.of("Y")).projectIds(List.of("P1")).statuses(List.of("APPROVED", "RETURNED"))
                .financialYear("2026-27").schemeCode("FC").fundSource("OSR").districtCode("CUTTACK")
                .createdBy("u1").fromDate(1L).toDate(2L).latestVersionOnly(true)
                .sortBy("projectId").sortOrder("asc").offset(20).limit(500)
                .build();
        List<Object> params = new ArrayList<>();
        String sql = builder.getSearchQuery(criteria, params);
        assertEquals(placeholders(sql), (long) params.size());
        assertFalse(sql.contains("od.cuttack"));
        assertTrue(sql.contains("ORDER BY ap.projectid ASC"));
        assertEquals(100, params.get(params.size() - 2));
        assertEquals(20, params.get(params.size() - 1));
    }

    @Test
    void unknownSortFallsBackToCreatedTimeAndStateTenantSearchesUlbs() {
        ActionPlanSearchCriteria criteria = ActionPlanSearchCriteria.builder().tenantId("od")
                .sortBy("1; DROP TABLE x").build();
        List<Object> params = new ArrayList<>();
        String sql = builder.getSearchQuery(criteria, params);
        assertTrue(sql.contains("ORDER BY ap.createdtime DESC"));
        assertFalse(sql.contains("DROP"));
        assertEquals(List.of("od", "od.%", 10, 0), params);
    }

    @Test
    void childQueryUsesPlaceholders() {
        List<Object> params = new ArrayList<>();
        String sql = builder.getChildQuery("eg_wms_ap_component", "actionplanid", List.of("1", "2", "3"),
                "componentnumber, id", params);
        assertEquals(3L, placeholders(sql));
        assertEquals(List.of("1", "2", "3"), params);
    }
}
