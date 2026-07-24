package org.egov.nationaldashboardingest.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.nationaldashboardingest.service.IngestService;
import org.egov.nationaldashboardingest.service.ExternalApiAuditLogger;
import org.egov.nationaldashboardingest.utils.ExternalApiAuditConstants;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.web.models.IngestRequest;
import org.egov.nationaldashboardingest.web.models.IngestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/metric")
public class MetricIngestController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngestService ingestService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private ExternalApiAuditLogger integrationAuditLogger;


     /*
     * It does the following:
        Receives a POST request with a JSON body mapped to IngestRequest.
        Logs the received data.
        Calls the ingestService.ingestData() method to process the data.
        Constructs a ResponseInfo using a factory.
        Builds an IngestResponse containing responseInfo and responseHash.
        Returns the response to the client.
     *
     */

    @RequestMapping(value="/_ingest", method = RequestMethod.POST)
    public ResponseEntity<IngestResponse> create(@RequestBody @Valid IngestRequest ingestRequest) {
        return integrationAuditLogger.logInboundApi(
                resolveCorrelationId(ingestRequest),
                resolveTenantId(ingestRequest),
                ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST,
                ingestRequest,
                () -> {
                    log.info("Received request: " + ingestRequest.getIngestData().toString());
                    List<Integer> responseHash = ingestService.ingestData(ingestRequest);
                    ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(
                            ingestRequest.getRequestInfo(), true);
                    IngestResponse response = IngestResponse.builder()
                            .responseInfo(responseInfo)
                            .responseHash(responseHash)
                            .build();
                    return new ResponseEntity<>(response, HttpStatus.OK);
                });
    }

    private String resolveCorrelationId(IngestRequest ingestRequest) {
        if (ingestRequest.getRequestInfo() != null
                && ingestRequest.getRequestInfo().getCorrelationId() != null
                && !ingestRequest.getRequestInfo().getCorrelationId().isBlank()) {
            return ingestRequest.getRequestInfo().getCorrelationId();
        }
        return null;
    }

    private String resolveTenantId(IngestRequest ingestRequest) {
        if (ingestRequest.getRequestInfo() != null
                && ingestRequest.getRequestInfo().getUserInfo() != null
                && ingestRequest.getRequestInfo().getUserInfo().getTenantId() != null) {
            return ingestRequest.getRequestInfo().getUserInfo().getTenantId();
        }
        if (ingestRequest.getIngestData() != null && !ingestRequest.getIngestData().isEmpty()) {
            return ingestRequest.getIngestData().get(0).getUlb();
        }
        return null;
    }
}
