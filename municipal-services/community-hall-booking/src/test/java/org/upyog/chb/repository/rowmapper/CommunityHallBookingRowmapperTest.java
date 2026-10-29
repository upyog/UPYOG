package org.upyog.chb.repository.rowmapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.upyog.chb.web.models.VenueBookingDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunityHallBookingRowmapperTest {

    @Mock
    private ResultSet resultSet;

    private CommunityHallBookingRowmapper rowmapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowmapper = new CommunityHallBookingRowmapper();
    }

    @Test
    void testExtractData() throws SQLException, DataAccessException {
        // Arrange
        when(resultSet.next()).thenReturn(true, false); // Simulate one row
        when(resultSet.getString("booking_id")).thenReturn("B001");
        when(resultSet.getString("booking_no")).thenReturn("BN001");
        when(resultSet.getString("tenant_id")).thenReturn("T001");
        when(resultSet.getString("venue_code")).thenReturn("CH001");
        when(resultSet.getString("booking_status")).thenReturn("CONFIRMED");
        when(resultSet.getString("special_category")).thenReturn("CATEGORY1");
        when(resultSet.getString("purpose")).thenReturn("Wedding");
        when(resultSet.getString("purpose_description")).thenReturn("Wedding Ceremony");
        when(resultSet.getLong("application_date")).thenReturn(1698768000000L);
        when(resultSet.getLong("payment_date")).thenReturn(1698854400000L);
        when(resultSet.getString("receipt_no")).thenReturn("R001");
        when(resultSet.getString("permission_letter_filestore_id")).thenReturn("PL001");
        when(resultSet.getString("payment_receipt_filestore_id")).thenReturn("PR001");

        // Act
        List<VenueBookingDetail> result = rowmapper.extractData(resultSet);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        VenueBookingDetail bookingDetail = result.get(0);
        assertEquals("B001", bookingDetail.getBookingId());
        assertEquals("BN001", bookingDetail.getBookingNo());
        assertEquals("T001", bookingDetail.getTenantId());
        assertEquals("CH001", bookingDetail.getVenueCode());
        assertEquals("CONFIRMED", bookingDetail.getBookingStatus());
        assertEquals("CATEGORY1", bookingDetail.getSpecialCategory().getCategory());
        assertEquals("Wedding", bookingDetail.getPurpose().getPurpose());
        assertEquals("Wedding Ceremony", bookingDetail.getPurposeDescription());
        assertEquals(1698768000000L, bookingDetail.getApplicationDate());
        assertEquals(1698854400000L, bookingDetail.getPaymentDate());
        assertEquals("R001", bookingDetail.getReceiptNo());
        assertEquals("PL001", bookingDetail.getPermissionLetterFilestoreId());
        assertEquals("PR001", bookingDetail.getPaymentReceiptFilestoreId());
    }
}