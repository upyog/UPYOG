package org.upyog.dx.upyogdata.pfms.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.dx.upyogdata.config.PFMSProperties;
import org.upyog.dx.upyogdata.constants.Constants;
import org.upyog.dx.upyogdata.pfms.models.PFMSAuthRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSAuthResponse;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransactionFormData;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.springframework.stereotype.Component;

/**
 * Handles all outbound HTTP calls to the PFMS external API.
 * Uses Feign clients internally for auth and data push operations.
 * Each call is explicitly audited via {@link ExternalApiAuditLogger}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PFMSApiClient {

    private final PFMSAuthFeignClient authFeignClient;
    private final PFMSDataFeignClient dataFeignClient;
    private final ExternalApiAuditLogger auditLogger;
    private final PFMSProperties pfmsProperties;

    /**
     * Authenticates with PFMS and returns a Bearer access token.
     * <p>
     * The audited lambda returns the full {@link PFMSAuthResponse} so {@code AccessToken}
     * and {@code RefreshToken} are masked in {@code ug_external_api_message_raw_detail}.
     * The live token is extracted after the wrap for use on subsequent calls.
     * </p>
     *
     * @return Bearer token string to use in subsequent API calls
     */
    public String fetchAccessToken() {
        log.info("Fetching access token from PFMS auth API");
        PFMSAuthRequest authRequest = new PFMSAuthRequest(
                pfmsProperties.getAuth().getUsername(), pfmsProperties.getAuth().getPassword());
        PFMSAuthResponse authResponse = auditLogger.logAndExecute(ExternalApiAuditDetail.builder()
                .tenantId(Constants.PFMS_TENANT)
                .externalApiName(ExternalApiAuditConstants.API_PFMS_AUTH)
                .requestPayload(authRequest)
                .endpoint(pfmsProperties.getAuth().getUrl())
                .method(Constants.HTTP_POST)
                .build(), () -> {
            PFMSAuthResponse response = authFeignClient.authenticate(authRequest);
            if (response == null || response.getAccessToken() == null) {
                throw new RuntimeException("PFMS auth response is null or token missing");
            }
            return response;
        });
        log.info("Pfms Access token fetched successfully");
        return authResponse.getAccessToken();
    }

    /**
     * Converts the transaction to form data and pushes it to PFMS.
     * Returns the raw response string from PFMS as-is.
     *
     * @param transaction the transaction to push
     * @param accessToken Bearer token from fetchAccessToken()
     * @return raw response body from PFMS
     */
    public String pushTransaction(PfmsTransactionEntity transaction, String accessToken) {
        return pushTransaction(transaction, accessToken, null, 0);
    }

    public String pushTransaction(PFMSTransaction transaction, String accessToken) {
        return pushTransaction(transaction, accessToken, null, 0);
    }

    /**
     * Pushes a transaction using an explicit audit correlation id so an in-cycle
     * retry (for example after HTTP 401) updates the same {@code pfms-data-push} row.
     * Pass {@code null} {@code auditCorrelationId} to allocate a new UUID (new logical request).
     *
     * @param transaction        voucher payload; bank-account fields are masked in audit JSON
     * @param accessToken        Bearer token from {@link #fetchAccessToken()}
     * @param auditCorrelationId table {@code correlation_id}; reuse on retry
     * @param retryCount         {@code 0} on first attempt, {@code 1} after 401 refresh
     */
    public String pushTransaction(PfmsTransactionEntity transaction, String accessToken,
            String auditCorrelationId, int retryCount) {
        log.info("Pushing transaction to Pfms | voucherNumber={}", transaction.getVoucherNumber());
        PFMSTransactionFormData formData = transaction.toFormData(pfmsProperties.getClient().getIp());
        String response = auditLogger.logAndExecute(ExternalApiAuditDetail.builder()
                .correlationId(auditCorrelationId)
                .retryCount(retryCount)
                .tenantId(Constants.PFMS_TENANT)
                .externalApiName(ExternalApiAuditConstants.API_PFMS_DATA_PUSH)
                .requestPayload(formData)
                .originatingCorrelationId(transaction.getCorrelationId())
                .businessReferenceId(transaction.getId())
                .endpoint(pfmsProperties.getData().getUrl())
                .method(Constants.HTTP_POST)
                .build(), () -> dataFeignClient.pushTransaction("Bearer " + accessToken, formData));
        log.info("Pfms Response | voucherNumber={} body={}", transaction.getVoucherNumber(), response);
        return response;
    }

    public String pushTransaction(PFMSTransaction transaction, String accessToken,
            String auditCorrelationId, int retryCount) {
        log.info("Pushing transaction to Pfms | voucherNumber={}", transaction.getVoucherNumber());
        PFMSTransactionFormData formData = PFMSTransactionFormData.from(transaction,
                pfmsProperties.getClient().getIp());
        String response = auditLogger.logAndExecute(ExternalApiAuditDetail.builder()
                .correlationId(auditCorrelationId)
                .retryCount(retryCount)
                .tenantId(Constants.PFMS_TENANT)
                .externalApiName(ExternalApiAuditConstants.API_PFMS_DATA_PUSH)
                .requestPayload(formData)
                .originatingCorrelationId(transaction.getCorrelationId())
                .businessReferenceId(transaction.getId())
                .endpoint(pfmsProperties.getData().getUrl())
                .method(Constants.HTTP_POST)
                .build(), () -> dataFeignClient.pushTransaction("Bearer " + accessToken, formData));
        log.info("Pfms Response | voucherNumber={} body={}", transaction.getVoucherNumber(), response);
        return response;
    }
}
