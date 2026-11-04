package org.upyog.dashboard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.constants.DashboardExtractorConstants;

class ExtractorUtilTest {

    @Test
    @DisplayName("resolveEffectiveTenantId joins list when present")
    void resolveEffectiveTenantId_joinsList() {
        String result = ExtractorUtil.resolveEffectiveTenantId(List.of("pg.citya", "pg.cityb"), Module.PT);
        assertEquals("pg.citya,pg.cityb", result);
    }

    @Test
    @DisplayName("resolveEffectiveTenantId throws IllegalArgumentException when tenantIds is empty or null")
    void resolveEffectiveTenantId_throwsWhenEmptyOrNull() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () ->
                ExtractorUtil.resolveEffectiveTenantId(List.of(), Module.PT));
        assertTrue(exception1.getMessage().contains("Tenant (ULB) missing for module PT"));

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () ->
                ExtractorUtil.resolveEffectiveTenantId(null, Module.PT));
        assertTrue(exception2.getMessage().contains("Tenant (ULB) missing for module PT"));
    }

    @Test
    @DisplayName("formatDate formats date to dd-MM-yyyy")
    void formatDate_formatsCorrectly() {
        LocalDate date = LocalDate.of(2026, 7, 15);
        assertEquals("15-07-2026", ExtractorUtil.formatDate(date));
        assertNull(ExtractorUtil.formatDate(null));
    }

    @Test
    @DisplayName("getStartOfDayUtcEpochMillis and getEndOfDayUtcEpochMillis calculate exact epoch range")
    void getStartAndEndOfDay_calculateExactRange() {
        LocalDate date = LocalDate.of(2026, 7, 15);
        long start = ExtractorUtil.getStartOfDayUtcEpochMillis(date);
        long end = ExtractorUtil.getEndOfDayUtcEpochMillis(date);

        assertTrue(end > start);
        assertEquals(86400000L - 1, end - start);
    }

    @Test
    @DisplayName("buildStandardQueryParams populates startTime, endTime, and tenantId")
    void buildStandardQueryParams_populatesCorrectParameters() {
        LocalDate date = LocalDate.of(2026, 7, 15);
        MapSqlParameterSource params = ExtractorUtil.buildStandardQueryParams("pg.citya,pg.cityb", date);

        assertNotNull(params);
        assertEquals("pg.citya,pg.cityb", params.getValue(DashboardExtractorConstants.PARAM_TENANT_ID));
        assertEquals(ExtractorUtil.getStartOfDayUtcEpochMillis(date), params.getValue(DashboardExtractorConstants.PARAM_START_TIME));
        assertEquals(ExtractorUtil.getEndOfDayUtcEpochMillis(date), params.getValue(DashboardExtractorConstants.PARAM_END_TIME));
    }
}
