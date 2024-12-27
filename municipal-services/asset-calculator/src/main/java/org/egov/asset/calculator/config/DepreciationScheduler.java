package org.egov.asset.calculator.config;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.services.ProcessDepreciation;
import org.egov.asset.calculator.services.ProcessDepreciationV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DepreciationScheduler {

    //private final ProcessDepreciation processDepreciation;
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
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Cron Job started at: {}", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            List<String> tenantIds = getTenantIds(); // Fetch tenant IDs dynamically
            // If tenantIds is null or empty, use default tenant ID from config
            if (tenantIds == null || tenantIds.isEmpty()) {
                String defaultTenantId = config.getDefaultTenantId(); // Fetch from configuration
                log.warn("No tenant IDs found. Using default tenant ID: {}", defaultTenantId);
                tenantIds = new ArrayList<>();
                tenantIds.add(defaultTenantId);
            }

            for (String tenantId : tenantIds) {
                log.info("Processing depreciation for tenant: {}", tenantId);
                //processDepreciation.executeBulkDepreciationProcedure(config.getDefaultTenantId());
                processDepreciation.calculateDepreciation(tenantId, null, config.getLegacyDataFlag());
            }
            LocalDateTime endTime = LocalDateTime.now();
            long durationInMillis = java.time.Duration.between(startTime, endTime).toMillis();
            log.info("Cron Job ended at: {}, Duration: {} ms",
                    endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    durationInMillis);
        } catch (Exception e) {
            log.error("Error during scheduled bulk depreciation: {}", e.getMessage(), e);
        }
    }

    public List<String> getTenantIds() {
        // Fetch tenant IDs from the database
        //List<String> tenantIds = tenantRepository.findAllTenantIds();
        //return tenantIds != null ? tenantIds : Collections.emptyList();
        return null;
    }
}
