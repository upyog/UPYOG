package org.egov.nationaldashboardingest.web.controllers;

import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.service.BulkIngestService;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitRequest;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit test suite for {@link BulkDataIngestController}.
 * <p>
 * Tests controller request dispatching, HTTP status resolution (200 OK vs 500 Internal Server Error),
 * and response body structure mapping from {@link BulkIngestStatus}.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class BulkDataIngestControllerTest {

    @Mock
    private BulkIngestService bulkIngestService;

    @Spy
    private ResponseInfoFactory responseInfoFactory = new ResponseInfoFactory();

    @InjectMocks
    private BulkDataIngestController bulkDataIngestController;

    private BulkIngestInitRequest request;
    private BulkIngestInitDetail details;

    /**
     * Prepares valid request and details fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() {
        details = new BulkIngestInitDetail("test.xlsx", "pb");
        request = BulkIngestInitRequest.builder()
                .details(details)
                .requestInfo(new RequestInfo())
                .build();
    }

    /**
     * Tests that status code 1 returns HTTP 200 OK with status "SUCCESS" and success message.
     */
    @Test
    void testInit_Success() {
        when(bulkIngestService.init(request)).thenReturn(1);

        ResponseEntity<BulkIngestInitResponse> responseEntity = bulkDataIngestController.init(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("SUCCESS", responseEntity.getBody().getStatus());
        assertEquals(1, responseEntity.getBody().getStatusCode());
        assertEquals("Bulk ingest init request pushed to queue successfully", responseEntity.getBody().getMessage());
        assertEquals(details, responseEntity.getBody().getDetails());
    }

    /**
     * Tests that status code 2 returns HTTP 200 OK with status "ALREADY_PRESENT" and warning message.
     */
    @Test
    void testInit_AlreadyPresent() {
        when(bulkIngestService.init(request)).thenReturn(2);

        ResponseEntity<BulkIngestInitResponse> responseEntity = bulkDataIngestController.init(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("ALREADY_PRESENT", responseEntity.getBody().getStatus());
        assertEquals(2, responseEntity.getBody().getStatusCode());
        assertEquals("File is already present in Kafka queue", responseEntity.getBody().getMessage());
    }

    /**
     * Tests that status code 3 returns HTTP 500 INTERNAL_SERVER_ERROR with status "FAILED".
     */
    @Test
    void testInit_Failed() {
        when(bulkIngestService.init(request)).thenReturn(3);

        ResponseEntity<BulkIngestInitResponse> responseEntity = bulkDataIngestController.init(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("FAILED", responseEntity.getBody().getStatus());
        assertEquals(3, responseEntity.getBody().getStatusCode());
        assertEquals("Failed to initialize bulk ingest request", responseEntity.getBody().getMessage());
    }
}
