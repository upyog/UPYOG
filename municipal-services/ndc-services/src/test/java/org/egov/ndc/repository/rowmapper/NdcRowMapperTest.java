package org.egov.ndc.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.egov.ndc.web.model.ndc.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NdcRowMapperTest {

    private NdcRowMapper rowMapper;
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        rowMapper = new NdcRowMapper();
        rs = mock(ResultSet.class);
    }

    @Test
    void testExtractDataWithSingleRow() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("a_uuid")).thenReturn("app-uuid");
        when(rs.getString("applicationno")).thenReturn("APP-001");
        when(rs.getString("tenantid")).thenReturn("tenant-1");
        when(rs.getString("reason")).thenReturn("Test Reason");
        when(rs.getString("applicationstatus")).thenReturn("ACTIVE");
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getString("a_createdby")).thenReturn("user-1");
        when(rs.getString("a_lastmodifiedby")).thenReturn("user-2");
        when(rs.getLong("a_createdtime")).thenReturn(123L);
        when(rs.getLong("a_lastmodifiedtime")).thenReturn(456L);

        // NdcDetails
        when(rs.getString("d_uuid")).thenReturn("details-uuid");
        when(rs.getString("d_applicationid")).thenReturn("app-uuid");
        when(rs.getString("businessservice")).thenReturn("NDC");
        when(rs.getString("consumercode")).thenReturn("consumer-1");
        when(rs.getBigDecimal("dueamount")).thenReturn(BigDecimal.valueOf(100.0));
        when(rs.getBoolean("isduepending")).thenReturn(true);
        when(rs.getString("status")).thenReturn("PENDING");

        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue("{\"key\":\"value\"}");
        when(rs.getObject("additionaldetails")).thenReturn(pgObject);

        // Document
        when(rs.getString("doc_uuid")).thenReturn("doc-uuid");
        when(rs.getString("doc_applicationid")).thenReturn("app-uuid");
        when(rs.getString("documenttype")).thenReturn("ID");
        when(rs.getString("documentattachment")).thenReturn("file.pdf");

        // Owner
        when(rs.getString("owner_uuid")).thenReturn("owner-uuid");

        List<Application> result = rowMapper.extractData(rs);

        assertEquals(1, result.size());
        Application app = result.get(0);
        assertEquals("APP-001", app.getApplicationNo());
        assertEquals("tenant-1", app.getTenantId());
        assertEquals("Test Reason", app.getReason());
        assertEquals("ACTIVE", app.getApplicationStatus());
        assertEquals(1, app.getNdcDetails().size());
        assertEquals(1, app.getDocuments().size());
        assertEquals(1, app.getOwners().size());

        JsonNode additionalDetails = app.getNdcDetails().get(0).getAdditionalDetails();
        assertNotNull(additionalDetails);
        assertEquals("value", additionalDetails.get("key").asText());
    }

    @Test
    void testExtractDataWithMultipleRowsForSameApplication() throws Exception {
        when(rs.next()).thenReturn(true, true, false); // two rows
        when(rs.getString("a_uuid")).thenReturn("app-uuid", "app-uuid"); // same application ID

        // First row: full data
        when(rs.getString("applicationno")).thenReturn("APP-001");
        when(rs.getString("tenantid")).thenReturn("tenant-1");
        when(rs.getString("reason")).thenReturn("Reason");
        when(rs.getString("applicationstatus")).thenReturn("ACTIVE");
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getString("a_createdby")).thenReturn("user-1");
        when(rs.getString("a_lastmodifiedby")).thenReturn("user-2");
        when(rs.getLong("a_createdtime")).thenReturn(123L);
        when(rs.getLong("a_lastmodifiedtime")).thenReturn(456L);

        // First row: one document and one owner
        when(rs.getString("doc_uuid")).thenReturn("doc-1", "doc-2");
        when(rs.getString("doc_applicationid")).thenReturn("app-uuid");
        when(rs.getString("documenttype")).thenReturn("ID");
        when(rs.getString("documentattachment")).thenReturn("file1.pdf", "file2.pdf");

        when(rs.getString("owner_uuid")).thenReturn("owner-1", "owner-2");

        // Details (same for both rows)
        when(rs.getString("d_uuid")).thenReturn("details-1", "details-1");
        when(rs.getString("d_applicationid")).thenReturn("app-uuid");
        when(rs.getString("businessservice")).thenReturn("NDC");
        when(rs.getString("consumercode")).thenReturn("consumer-1");
        when(rs.getBigDecimal("dueamount")).thenReturn(BigDecimal.valueOf(100.0));
        when(rs.getBoolean("isduepending")).thenReturn(true);
        when(rs.getString("status")).thenReturn("PENDING");

        PGobject pg = new PGobject();
        pg.setType("jsonb");
        pg.setValue("{\"key\":\"value\"}");
        when(rs.getObject("additionaldetails")).thenReturn(pg);

        List<Application> result = rowMapper.extractData(rs);

        assertEquals(1, result.size());
        Application app = result.get(0);
        assertEquals(2, app.getDocuments().size());
        assertEquals(2, app.getOwners().size());
    }

    @Test
    void testExtractDataThrowsJsonProcessingException() throws Exception {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("a_uuid")).thenReturn("app-uuid");
        when(rs.getString("applicationno")).thenReturn("APP-001");
        when(rs.getString("tenantid")).thenReturn("tenant-1");
        when(rs.getString("reason")).thenReturn("Reason");
        when(rs.getString("applicationstatus")).thenReturn("ACTIVE");
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getString("a_createdby")).thenReturn("user-1");
        when(rs.getString("a_lastmodifiedby")).thenReturn("user-2");
        when(rs.getLong("a_createdtime")).thenReturn(123L);
        when(rs.getLong("a_lastmodifiedtime")).thenReturn(456L);

        // Required to trigger addNdcDetails
        when(rs.getString("d_uuid")).thenReturn("details-uuid");
        when(rs.getString("d_applicationid")).thenReturn("app-uuid");
        when(rs.getString("businessservice")).thenReturn("NDC");
        when(rs.getString("consumercode")).thenReturn("consumer-1");
        when(rs.getBigDecimal("dueamount")).thenReturn(BigDecimal.valueOf(100.0));
        when(rs.getBoolean("isduepending")).thenReturn(true);
        when(rs.getString("status")).thenReturn("PENDING");

        // Malformed JSON to trigger JsonProcessingException
        PGobject malformedJson = new PGobject();
        malformedJson.setType("jsonb");
        malformedJson.setValue("{invalid:json"); // deliberately malformed
        when(rs.getObject("additionaldetails")).thenReturn(malformedJson);

        assertThrows(RuntimeException.class, () -> rowMapper.extractData(rs));
    }

    @Test
    void testGetJsonValueWithNullPGObject() {
        assertNull(rowMapper.getJsonValue(null));
    }

    @Test
    void testGetJsonValueWithEmptyValue() throws Exception {
        PGobject pg = new PGobject();
        pg.setValue(null);
        assertNull(rowMapper.getJsonValue(pg));
    }

    @Test
    void testGetJsonValueWithInvalidJsonThrowsCustomException() throws SQLException {
        PGobject pg = new PGobject();
        pg.setValue("invalid-json");

        Exception ex = assertThrows(RuntimeException.class, () -> rowMapper.getJsonValue(pg));
        assertTrue(ex.getMessage().contains("json exception"));
    }
}