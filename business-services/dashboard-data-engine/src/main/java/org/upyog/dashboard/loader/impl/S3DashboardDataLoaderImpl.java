package org.upyog.dashboard.loader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.SXSSFExcelGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy implementation of {@link DashboardDataLoader} that aggregates normalized
 * dashboard payload records into an Excel file and routes the upload through
 * {@link DashboardIngestionClient}.
 */
@Slf4j
@Component("s3DataLoader")
@RequiredArgsConstructor
public class S3DashboardDataLoaderImpl implements DashboardDataLoader {

    private final DashboardIngestionClient ingestionClient;
    private final SXSSFExcelGeneratorService excelGeneratorService;
    private final DashboardProperties properties;

    @Override
    public IngestionResult load(DashboardPayload payload) {
        String moduleName = "DASHBOARD";
        if (payload.getData() != null && !payload.getData().isEmpty() && payload.getData().get(0).getModule() != null) {
            moduleName = payload.getData().get(0).getModule();
        }

        log.info("S3DashboardDataLoaderImpl | Executing S3 upload routing for module: {}", moduleName);
        File tempFile = null;
        try {
            List<Object> records = new ArrayList<>();
            if (payload.getData() != null) {
                for (DashboardData data : payload.getData()) {
                    records.add(data);
                }
            }

            tempFile = excelGeneratorService.generateExcelFile(moduleName, records);

            String tenantId = properties.getTenantId();
            if (payload.getData() != null && !payload.getData().isEmpty()) {
                String payloadUlb = payload.getData().get(0).getUlb();
                if (payloadUlb != null && payloadUlb.contains(".")) {
                    tenantId = payloadUlb.split("\\.")[0];
                } else if (payloadUlb != null) {
                    tenantId = payloadUlb;
                }
            }

            return ingestionClient.uploadToS3(tempFile, moduleName, tenantId);
        } catch (Exception e) {
            log.error("S3DashboardDataLoaderImpl | Failed to generate and upload Excel file for module {}", moduleName, e);
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Exception during S3 routing: " + e.getMessage())
                    .build();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
