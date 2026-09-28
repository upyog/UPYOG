package org.upyog.dashboard.loader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.IngestionRecordPersistenceService;
import org.upyog.dashboard.service.SXSSFExcelGeneratorService;
import org.upyog.dashboard.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy implementation of {@link DashboardDataLoader} that aggregates normalized
 * dashboard payload records into a dataset file (Excel or Delimited), uploads the file to AWS S3 storage,
 * and persists the ingestion audit records via {@link IngestionRecordPersistenceService}.
 */
@Slf4j
@Component("s3DataLoader")
@RequiredArgsConstructor
public class S3DashboardDataLoaderImpl implements DashboardDataLoader {

    private final DashboardIngestionClient ingestionClient;
    private final SXSSFExcelGeneratorService excelGeneratorService;
    private final DashboardProperties properties;
    private final IngestionRecordPersistenceService persistenceService;
    private final ObjectMapper objectMapper;

    @Override
    public IngestionResult load(DashboardPayload payload) {
        String moduleName = "DASHBOARD";
        if (payload.getData() != null && !payload.getData().isEmpty() && payload.getData().get(0).getModule() != null) {
            moduleName = payload.getData().get(0).getModule();
        }

        int payloadDataSize = (payload.getData() != null) ? payload.getData().size() : 0;
        DashboardData firstElement = (payloadDataSize > 0) ? payload.getData().get(0) : null;
        String ingestionDateString = firstElement != null ? firstElement.getDate() : null;

        log.info("S3DashboardDataLoaderImpl | Executing S3 upload routing for module: {}", moduleName);
        File generatedFile = null;
        String requestJson = null;
        try {
            List<Object> records = new ArrayList<>();
            if (payload.getData() != null) {
                for (DashboardData data : payload.getData()) {
                    records.add(data);
                }
            }

            try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelGeneratorService.createStreamingSession(moduleName, DashboardConstants.DAILY)) {
                session.appendBatchRecords(records);
                generatedFile = session.finishWorkbook();
            }

            String tenantId = properties.getTenantId();
            if (payload.getData() != null && !payload.getData().isEmpty()) {
                String payloadUlb = payload.getData().get(0).getUlb();
                if (payloadUlb != null && payloadUlb.contains(".")) {
                    tenantId = payloadUlb.split("\\.")[0];
                } else if (payloadUlb != null) {
                    tenantId = payloadUlb;
                }
            }

            try {
                requestJson = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                requestJson = "{\"moduleName\":\"" + moduleName + "\",\"tenantId\":\"" + tenantId + "\"}";
            }

            String delimiter = (properties != null && (properties.isDelimitedFileEnabled() || properties.isPipeFileEnabled()))
                    ? properties.getPipeFileDelimiter()
                    : null;

            IngestionResult clientResult = ingestionClient.uploadToS3(generatedFile, moduleName, tenantId, delimiter);

            IngestionResult finalResult = IngestionResult.builder()
                    .ingestionStatus(clientResult.getIngestionStatus())
                    .responseData(clientResult.getResponseData())
                    .failureReason(clientResult.getFailureReason())
                    .date(ingestionDateString)
                    .moduleName(moduleName)
                    .ingestedAt(CommonUtils.getCurrentEpochMillis())
                    .build();

            pushIngestionRecord(payload, requestJson,
                    clientResult.getResponseData() != null ? clientResult.getResponseData() : clientResult.getFailureReason(),
                    clientResult.getIngestionStatus());

            return finalResult;
        } catch (Exception exception) {
            log.error("S3DashboardDataLoaderImpl | Failed to generate and upload dataset file for module {}", moduleName, exception);
            String failureReason = "Exception during S3 routing: " + exception.getMessage();
            IngestionResult failureResult = IngestionResult.builder()
                    .ingestionStatus(DashboardConstants.STATUS_FAILURE)
                    .failureReason(failureReason)
                    .date(ingestionDateString)
                    .moduleName(moduleName)
                    .ingestedAt(CommonUtils.getCurrentEpochMillis())
                    .build();

            pushIngestionRecord(payload, requestJson, failureReason, DashboardConstants.STATUS_FAILURE);
            return failureResult;
        } finally {
            if (generatedFile != null && generatedFile.exists()) {
                boolean keepFile = (properties != null && (properties.isDelimitedFileEnabled() || properties.isPipeFileEnabled()))
                        ? properties.isDelimitedKeepFile()
                        : false;
                if (!keepFile) {
                    generatedFile.delete();
                }
            }
        }
    }

    private void pushIngestionRecord(DashboardPayload payload, String requestJson, String responseOrError, String status) {
        try {
            persistenceService.pushIngestionRecord(payload, requestJson, responseOrError, status);
        } catch (Exception exception) {
            log.error("S3DashboardDataLoaderImpl | Failed to push ingestion record to persistence service", exception);
        }
    }
}
