package org.egov.nationaldashboardingest.service;

import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.service.impl.BulkIngestServiceImpl;
import org.egov.nationaldashboardingest.utils.BulkIngestConstants;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitRequest;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for {@link BulkIngestServiceImpl}.
 * <p>
 * Tests validation of {@link BulkIngestInitRequest}, message publishing via {@link Producer},
 * and translation of producer exceptions into standardized status codes.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class BulkIngestServiceTest {

    @Mock
    private Producer producer;

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private BulkIngestServiceImpl bulkIngestService;

    private BulkIngestInitRequest validRequest;
    private BulkIngestInitDetail validDetail;

    /**
     * Initializes valid request objects before each test.
     */
    @BeforeEach
    void setUp() {
        validDetail = new BulkIngestInitDetail("test-file.xlsx", "pb");
        validRequest = BulkIngestInitRequest.builder()
                .details(validDetail)
                .build();
    }

    /**
     * Tests that a valid request produces a message on the configured bulk ingest Kafka topic
     * and returns the SUCCESS status code (1).
     */
    @Test
    void testInit_Success() {
        when(applicationProperties.getBulkIngestTopic()).thenReturn("bulk-ingest-init");
        doNothing().when(producer).push("bulk-ingest-init", "test-file.xlsx", validDetail);

        Integer response = bulkIngestService.init(validRequest);

        assertEquals(BulkIngestConstants.STATUS_CODE_SUCCESS, response);
        verify(producer, times(1)).push("bulk-ingest-init", "test-file.xlsx", validDetail);
    }

    /**
     * Tests that a request with missing or blank mandatory fields throws a {@link CustomException}.
     */
    @Test
    void testInit_InvalidRequest_ThrowsException() {
        BulkIngestInitRequest invalidRequest = BulkIngestInitRequest.builder()
                .details(new BulkIngestInitDetail("", "pb"))
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> bulkIngestService.init(invalidRequest));
        assertEquals(BulkIngestConstants.ERR_CODE_INVALID_INIT_REQUEST, exception.getCode());
    }

    /**
     * Tests that when Kafka publishing throws a duplicate file exception, the service returns ALREADY_PRESENT (2).
     */
    @Test
    void testInit_DuplicateFile_Returns2() {
        when(applicationProperties.getBulkIngestTopic()).thenReturn("bulk-ingest-init");
        doThrow(new CustomException(BulkIngestConstants.ERR_CODE_DUPLICATE_FILE, "File already in kafka"))
                .when(producer).push("bulk-ingest-init", "test-file.xlsx", validDetail);

        Integer response = bulkIngestService.init(validRequest);

        assertEquals(BulkIngestConstants.STATUS_CODE_ALREADY_PRESENT, response);
    }

    /**
     * Tests that unexpected Kafka failures result in the FAILED status code (3).
     */
    @Test
    void testInit_KafkaFailure_Returns3() {
        when(applicationProperties.getBulkIngestTopic()).thenReturn("bulk-ingest-init");
        doThrow(new RuntimeException("Kafka connection down"))
                .when(producer).push("bulk-ingest-init", "test-file.xlsx", validDetail);

        Integer response = bulkIngestService.init(validRequest);

        assertEquals(BulkIngestConstants.STATUS_CODE_FAILED, response);
    }
}
