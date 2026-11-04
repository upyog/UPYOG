package org.upyog.dashboard.service.impl;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.model.BulkIngestDetails;
import org.upyog.dashboard.model.BulkIngestRequest;
import org.upyog.dashboard.model.RequestInfo;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.service.BulkIngestionInitService;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Production implementation of {@link BulkIngestionInitService}.
 * <p>
 * Orchestrates the downstream notification workflow after files are staged in AWS S3:
 * <ol>
 *   <li>Retrieves active OAuth authentication credentials and system user metadata from {@link OAuthTokenService}.</li>
 *   <li>Assembles standard UPYOG {@link RequestInfo} with API ID, auth token, and epoch message ID.</li>
 *   <li>Constructs {@link BulkIngestDetails} linking the S3 file key with the state tenant code and optional delimiter.</li>
 *   <li>Wraps both in a {@link BulkIngestRequest} envelope and serializes the JSON body via {@link ObjectMapper}.</li>
 *   <li>Dispatches the HTTP POST request to the national dashboard endpoint using {@link DashboardFeignClient#ingestMetrics}.</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkIngestionInitServiceImpl implements BulkIngestionInitService {

    private final DashboardFeignClient dashboardFeignClient;
    private final OAuthTokenService oAuthTokenService;
    private final DashboardProperties dashboardProperties;
    private final ObjectMapper objectMapper;

    @Override
    public String initializeBulkIngestion(String fileStoreId) {
        String defaultDelimiter = null;
        if (dashboardProperties != null && (dashboardProperties.isDelimitedFileEnabled() || dashboardProperties.isPipeFileEnabled())) {
            defaultDelimiter = dashboardProperties.getPipeFileDelimiter();
        }
        return initializeBulkIngestion(fileStoreId, defaultDelimiter);
    }

    @Override
    public String initializeBulkIngestion(String fileStoreId, String delimiter) {
        String bulkInitUrl = dashboardProperties.getBulkInitUrl();
        String stateCode = dashboardProperties.getTenantId();
        log.info("BulkIngestionInitServiceImpl | Initializing bulk ingestion at: {} for fileName: {}, stateCode: {}, delimiter: {}",
                bulkInitUrl, fileStoreId, stateCode, delimiter);

        try {
            String oauthToken = oAuthTokenService != null ? oAuthTokenService.getToken() : null;
            UserInfo userInfo = oAuthTokenService != null ? oAuthTokenService.getUserInfo() : null;

            RequestInfo requestInfo = RequestInfo.builder()
                    .apiId(DashboardConstants.API_ID_RAINMAKER)
                    .authToken(oauthToken)
                    .userInfo(userInfo)
                    .msgId(CommonUtils.getCurrentEpochMillis() + DashboardConstants.LOCALE_EN_IN_SUFFIX)
                    .build();

            BulkIngestDetails details = BulkIngestDetails.builder()
                    .fileName(fileStoreId)
                    .stateCode(stateCode)
                    .delimiter(delimiter)
                    .build();

            BulkIngestRequest requestPayload = BulkIngestRequest.builder()
                    .requestInfo(requestInfo)
                    .details(details)
                    .build();

            String payloadJson = objectMapper.writeValueAsString(requestPayload);
            log.info("BulkIngestionInitServiceImpl | Bulk ingest init request payload: {}", payloadJson);

            String responseJson = dashboardFeignClient.ingestMetrics(URI.create(bulkInitUrl), payloadJson);
            log.info("BulkIngestionInitServiceImpl | Bulk ingest init response: {}", responseJson);
            return responseJson;
        } catch (Exception exception) {
            log.error("BulkIngestionInitServiceImpl | Failed to call bulk ingest init API at {}", bulkInitUrl, exception);
            return null;
        }
    }
}
