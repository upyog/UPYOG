package org.egov.nationaldashboardingest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Unit test suite for {@link DelimitedStreamingBatchReader}.
 * <p>
 * Tests pipe-delimited (|) and comma-delimited (,) text file parsing,
 * auto-detection of delimiters, and automatic un-flattening of dot-notation
 * column headers into nested UPYOG metric buckets.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class DelimitedStreamingBatchReaderTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private DelimitedStreamingBatchReader delimitedStreamingBatchReader;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(delimitedStreamingBatchReader, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(delimitedStreamingBatchReader, "applicationProperties", applicationProperties);
        lenient().when(applicationProperties.getBatchSize()).thenReturn(100);
    }


    @Test
    void testProcessDelimitedFileInBatches_PipeDelimited_UnflatteningSuccess() throws IOException {
        File tempFile = File.createTempFile("pt_bulk_", ".psv");
        tempFile.deleteOnExit();

        String header = "date|module|state|ulb|ward|region|assessments|cess.usageCategory.RESIDENTIAL|cess.usageCategory.COMMERCIAL|todaysCollection.paymentChannelType.Digital|propertiesRegistered.financialYear.2026-27\n";
        String row1 = "15-09-2024|PT|pg|pg.citya|Block 4|Test|0|10.5|20.0|100.0|35423\n";

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(header);
            writer.write(row1);
        }

        List<IngestRowData> resultList = new ArrayList<>();
        int rowsProcessed = delimitedStreamingBatchReader.processDelimitedFileInBatches(tempFile, null, resultList::addAll);

        assertEquals(1, rowsProcessed);
        assertEquals(1, resultList.size());

        Data data = resultList.get(0).getData();
        assertNotNull(data);
        assertEquals("15-09-2024", data.getDate());
        assertEquals("PT", data.getModule());
        assertEquals("pg", data.getState());
        assertEquals("pg.citya", data.getUlb());
        assertEquals("Block 4", data.getWard());
        assertEquals("Test", data.getRegion());

        Map<String, Object> metrics = data.getMetrics();
        assertNotNull(metrics);

        // Scalar metric check
        assertEquals(0, metrics.get("assessments"));

        // Bucket metric check for cess (usageCategory)
        assertTrue(metrics.containsKey("cess"));
        List<Map<String, Object>> cessGroups = (List<Map<String, Object>>) metrics.get("cess");
        assertEquals(1, cessGroups.size());
        assertEquals("usageCategory", cessGroups.get(0).get("groupBy"));

        List<Map<String, Object>> cessBuckets = (List<Map<String, Object>>) cessGroups.get(0).get("buckets");
        assertEquals(2, cessBuckets.size());
        assertEquals("RESIDENTIAL", cessBuckets.get(0).get("name"));
        assertEquals(10.5, cessBuckets.get(0).get("value"));
        assertEquals("COMMERCIAL", cessBuckets.get(1).get("name"));
        assertEquals(20.0, cessBuckets.get(1).get("value"));

        // Bucket metric check for todaysCollection (paymentChannelType)
        assertTrue(metrics.containsKey("todaysCollection"));
        List<Map<String, Object>> collectionGroups = (List<Map<String, Object>>) metrics.get("todaysCollection");
        assertEquals("paymentChannelType", collectionGroups.get(0).get("groupBy"));

        // Bucket metric check for propertiesRegistered (financialYear)
        assertTrue(metrics.containsKey("propertiesRegistered"));
        List<Map<String, Object>> propGroups = (List<Map<String, Object>>) metrics.get("propertiesRegistered");
        assertEquals("financialYear", propGroups.get(0).get("groupBy"));
        List<Map<String, Object>> propBuckets = (List<Map<String, Object>>) propGroups.get(0).get("buckets");
        assertEquals("2026-27", propBuckets.get(0).get("name"));
        assertEquals(35423, propBuckets.get(0).get("value"));
    }

    @Test
    void testProcessDelimitedFileInBatches_CommaDelimited_Success() throws IOException {
        File tempFile = File.createTempFile("pgr_bulk_", ".csv");
        tempFile.deleteOnExit();

        String header = "date,module,state,ulb,ward,region,todaysApplications,closedApplications\n";
        String row1 = "16-09-2024,PGR,pb,pb.amritsar,Ward 1,Amritsar Region,50,45\n";

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(header);
            writer.write(row1);
        }

        List<IngestRowData> resultList = new ArrayList<>();
        int rowsProcessed = delimitedStreamingBatchReader.processDelimitedFileInBatches(tempFile, null, resultList::addAll);

        assertEquals(1, rowsProcessed);
        assertEquals(1, resultList.size());

        Data data = resultList.get(0).getData();
        assertEquals("PGR", data.getModule());
        assertEquals(50, data.getMetrics().get("todaysApplications"));
        assertEquals(45, data.getMetrics().get("closedApplications"));
    }

    @Test
    void testProcessDelimitedFileInBatches_WithUuidColumn_Success() throws IOException {
        File tempFile = File.createTempFile("pt_uuid_", ".psv");
        tempFile.deleteOnExit();

        String header = "date|module|state|ulb|uuid|assessments\n";
        String row1 = "15-09-2024|PT|pg|pg.citya|b3177c56-a9f2-4b59-a00c-9e9c38b5f28f|100\n";

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(header);
            writer.write(row1);
        }

        List<IngestRowData> resultList = new ArrayList<>();
        int rowsProcessed = delimitedStreamingBatchReader.processDelimitedFileInBatches(tempFile, null, resultList::addAll);

        assertEquals(1, rowsProcessed);
        assertEquals(1, resultList.size());

        IngestRowData rowData = resultList.get(0);
        assertNotNull(rowData.getRequestInfo());
        assertNotNull(rowData.getRequestInfo().getUserInfo());
        assertEquals("b3177c56-a9f2-4b59-a00c-9e9c38b5f28f", rowData.getRequestInfo().getUserInfo().getUuid());
        assertEquals("pg.citya", rowData.getRequestInfo().getUserInfo().getTenantId());
    }
}
