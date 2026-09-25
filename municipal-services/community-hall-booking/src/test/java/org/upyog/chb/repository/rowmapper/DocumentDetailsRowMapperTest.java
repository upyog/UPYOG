package org.upyog.chb.repository.rowmapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.DocumentDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentDetailsRowMapperTest {

    @Mock
    private ResultSet resultSet;

    private DocumentDetailsRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowMapper = new DocumentDetailsRowMapper();
    }

    @Test
    void testExtractData() throws SQLException, DataAccessException {
        // Arrange
        when(resultSet.next()).thenReturn(true, false); // Simulate one row
        when(resultSet.getString("document_detail_id")).thenReturn("D001");
        when(resultSet.getString("booking_id")).thenReturn("B001");
        when(resultSet.getString("document_type")).thenReturn("Type1");
        when(resultSet.getString("filestore_id")).thenReturn("FS001");
        when(resultSet.getString("createdby")).thenReturn("User1");
        when(resultSet.getLong("createdtime")).thenReturn(1698768000000L);
        when(resultSet.getString("lastmodifiedby")).thenReturn("User2");
        when(resultSet.getLong("lastmodifiedtime")).thenReturn(1698854400000L);

        // Act
        List<DocumentDetail> result = rowMapper.extractData(resultSet);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        DocumentDetail detail = result.get(0);
        assertEquals("D001", detail.getDocumentDetailId());
        assertEquals("B001", detail.getBookingId());
        assertEquals("Type1", detail.getDocumentType());
        assertEquals("FS001", detail.getFileStoreId());

        AuditDetails auditDetails = detail.getAuditDetails();
        assertNotNull(auditDetails);
        assertEquals("User1", auditDetails.getCreatedBy());
        assertEquals(1698768000000L, auditDetails.getCreatedTime());
        assertEquals("User2", auditDetails.getLastModifiedBy());
        assertEquals(1698854400000L, auditDetails.getLastModifiedTime());
    }
}