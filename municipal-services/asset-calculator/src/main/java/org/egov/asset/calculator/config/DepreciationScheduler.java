package org.egov.asset.calculator.config;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.services.ProcessDepreciationV2;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class DepreciationScheduler {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(SYSTEM_ZONE);

    private final ProcessDepreciationV2 processDepreciation;
    private final CalculatorConfig config;

    @Autowired
    public DepreciationScheduler(ProcessDepreciationV2 processDepreciation, CalculatorConfig config) {
        this.processDepreciation = processDepreciation;
        this.config = config;
    }

    /**
     * Schedules bulk asset depreciation daily at a configurable time.
     */
    @Scheduled(cron = "${scheduler.cron}")
    public void scheduleBulkDepreciation() {
        Instant startTime = Instant.now();
        log.info("Cron Job started at: {}", LOG_TIMESTAMP_FORMAT.format(startTime));

        try {
            List<String> tenantIds = getTenantIds();
            log.info("List of Tenant IDs to be processed in scheduler: {}", tenantIds.toString());
            if (tenantIds.isEmpty()) {
                String defaultTenantId = config.getDefaultTenantId();
                log.warn("No tenant IDs found. Using default tenant ID: {}", defaultTenantId);
                tenantIds = new ArrayList<>();
                tenantIds.add(defaultTenantId);
            }

            for (String tenantId : tenantIds) {
                processDepreciationForTenant(tenantId);
            }

            Instant endTime = Instant.now();
            long durationInMillis = Duration.between(startTime, endTime).toMillis();
            log.info("Cron Job ended at: {}, Duration: {} ms",
                    LOG_TIMESTAMP_FORMAT.format(endTime),
                    durationInMillis);
        } catch (Exception e) {
            log.error("Error during scheduled bulk depreciation: {}", e.getMessage(), e);
        }
    }

    private void processDepreciationForTenant(String tenantId) {
        log.info("Processing depreciation for tenant: {}", tenantId);

        try {
            processDepreciation.calculateDepreciation(
                    tenantId,
                    null,
                    config.getLegacyDataFlag(),
                    CalculatorConstants.USER
            );
        } catch (Exception e) {
            log.error("Error processing depreciation for tenant: {}. Error: {}", tenantId, e.getMessage(), e);
        }
    }

    public List<String> getTenantIds() {
        String depreciationProcessTenantIds = config.getDepreciationProcessTenantIds();
        log.info("Tenants To be processed : {}", depreciationProcessTenantIds);
        if (depreciationProcessTenantIds != null && !depreciationProcessTenantIds.isEmpty()) {
            List<String> tenantIdList = Arrays.stream(depreciationProcessTenantIds.split(","))
                    .map(String::trim)
                    .toList();

            log.info("List of Tenant IDs to be processed: {}", tenantIdList.toString());

            return tenantIdList;
        }
        return Collections.emptyList();
    }
}
