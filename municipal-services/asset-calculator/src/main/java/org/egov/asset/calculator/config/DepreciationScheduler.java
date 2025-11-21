package org.egov.asset.calculator.config;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.services.ProcessDepreciationV2;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            log.info("List of Tenant IDs to be processed in scheduler: {}", tenantIds.toString());
            // If tenantIds is null or empty, use default tenant ID from config
            if (tenantIds.isEmpty()) {
                String defaultTenantId = config.getDefaultTenantId(); // Fetch from configuration
                log.warn("No tenant IDs found. Using default tenant ID: {}", defaultTenantId);
                tenantIds = new ArrayList<>();
                tenantIds.add(defaultTenantId);
            }

            for (String tenantId : tenantIds) {
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
            } // for loop for processing depreciation of the listed tenants

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
        String depreciationProcessTenantIds = config.getDepreciationProcessTenantIds();
        log.info("Tenants To be processed : {}", depreciationProcessTenantIds);
        if (depreciationProcessTenantIds != null && !depreciationProcessTenantIds.isEmpty()) {
            List<String> tenantIdList = Arrays.stream(depreciationProcessTenantIds.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            // Log the processed list of tenant IDs
            log.info("List of Tenant IDs to be processed: {}", tenantIdList.toString());

            return tenantIdList;
        }
        // Return an empty list if the configuration is missing or empty
        return Collections.emptyList();
    }
}
