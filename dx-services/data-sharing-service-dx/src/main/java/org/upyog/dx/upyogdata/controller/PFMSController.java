package org.upyog.dx.upyogdata.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.upyog.dx.upyogdata.constants.Constants;
import org.upyog.dx.upyogdata.pfms.client.PFMSApiClient;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.service.PFMSForwardingService;
import org.upyog.dx.upyogdata.pfms.service.PFMSService;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

/**
 * REST controller exposing APIs for PFMS transaction data exchange.
 * <p>
 * State systems post inbound payloads on {@code /_create}. That inbound call is audited
 * with {@link ExternalApiAuditLogger#logInboundApi}. Outbound PFMS HTTP is audited in
 * {@link PFMSApiClient}, not here.
 * </p>
 */
@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Upyog Data Sharing Service", description = "APIs for Upyog Data Sharing Services")
public class PFMSController {

    private final PFMSService pfmsService;
    private final PFMSForwardingService pfmsForwardingService;
    private final ExternalApiAuditLogger auditLogger;

    /**
     * Inbound create from a state system. Audited as {@code state-pfms-transaction-create} / INBOUND.
     * {@link IllegalArgumentException} from the service still produces a FAILED audit row, then HTTP 400.
     */
    @PostMapping("/_create")
    @Operation(summary = "Create", description = "State will push data in this create")
    public ResponseEntity<?> createTransaction(
            @Valid @RequestBody PFMSCreateTransactionRequest request) {
        try {
            Object result = auditLogger.logInboundApi(ExternalApiAuditDetail.builder()
                    .tenantId(Constants.PFMS_TENANT)
                    .externalApiName(ExternalApiAuditConstants.API_STATE_PFMS_TRANSACTION_CREATE)
                    .requestPayload(request)
                    .originatingCorrelationId(resolveOriginatingCorrelationId(request))
                    .method(Constants.HTTP_POST)
                    .endpoint(Constants.PFMS_INBOUND_CREATE_ENDPOINT)
                    .build(), () -> pfmsService.createTransaction(request));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/scheduler/_trigger")
    @Operation(summary = "Manually trigger PFMS forwarding scheduler", description = "For testing only")
    public ResponseEntity<?> triggerScheduler() {
        pfmsForwardingService.forwardInitiatedTransactions(Constants.MANUAL);
        return ResponseEntity.ok("Scheduler triggered");
    }

    @PostMapping("/scheduler/_retryTrigger")
    @Operation(summary = "Manually trigger PFMS Retry scheduler for Failed Status", description = "For testing only")
    public ResponseEntity<?> triggerRetryScheduler() {
        pfmsForwardingService.retryFailedTransactions(Constants.MANUAL);
        return ResponseEntity.ok("Retry Scheduler triggered");
    }

    private String resolveOriginatingCorrelationId(PFMSCreateTransactionRequest request) {
        if (request.getRequestInfo() != null
                && request.getRequestInfo().getCorrelationId() != null
                && !request.getRequestInfo().getCorrelationId().isBlank()) {
            return request.getRequestInfo().getCorrelationId();
        }
        return null;
    }
}
