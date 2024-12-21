package org.egov.asset.calculator.config;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.services.ProcessDepreciation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DepreciationScheduler {

    private final ProcessDepreciation processDepreciation;
    private final CalculatorConfig config;

    @Autowired
    public DepreciationScheduler(ProcessDepreciation processDepreciation, CalculatorConfig config) {
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
            processDepreciation.executeBulkDepreciationProcedure(config.getDefaultTenantId());
            LocalDateTime endTime = LocalDateTime.now();
            long durationInMillis = java.time.Duration.between(startTime, endTime).toMillis();
            log.info("Cron Job ended at: {}, Duration: {} ms",
                    endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    durationInMillis);
        } catch (Exception e) {
            log.error("Error during scheduled bulk depreciation: {}", e.getMessage(), e);
        }
    }
}
