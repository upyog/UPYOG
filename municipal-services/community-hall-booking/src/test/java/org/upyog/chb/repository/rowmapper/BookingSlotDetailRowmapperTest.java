package org.upyog.chb.repository.rowmapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingSlotDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingSlotDetailRowmapperTest {

    @Mock
    private ResultSet resultSet;

    private BookingSlotDetailRowmapper rowmapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowmapper = new BookingSlotDetailRowmapper();
    }

    @Test
    void testExtractData() throws SQLException, DataAccessException {
        // Arrange
        when(resultSet.next()).thenReturn(true, false); // Simulate one row
        when(resultSet.getString("slot_id")).thenReturn("slot1");
        when(resultSet.getString("booking_id")).thenReturn("booking1");
        when(resultSet.getString("unit_code")).thenReturn("hall1");
        when(resultSet.getString("booking_date")).thenReturn("2023-11-01");
        when(resultSet.getString("booking_from_time")).thenReturn("10:00");
        when(resultSet.getString("booking_to_time")).thenReturn("12:00");
        when(resultSet.getString("status")).thenReturn("CONFIRMED");
        when(resultSet.getString("capacity")).thenReturn("100");

        try (var mockedUtil = mockStatic(CommunityHallBookingUtil.class)) {
            mockedUtil.when(() -> CommunityHallBookingUtil.parseStringToLocalDate("2023-11-01"))
                    .thenReturn(LocalDate.of(2023, Month.NOVEMBER, 1));
            mockedUtil.when(() -> CommunityHallBookingUtil.getAuditDetails(resultSet))
                    .thenReturn(null);

            // Act
            List<BookingSlotDetail> result = rowmapper.extractData(resultSet);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            BookingSlotDetail slotDetail = result.get(0);
            assertEquals("slot1", slotDetail.getSlotId());
            assertEquals("booking1", slotDetail.getBookingId());
            assertEquals("hall1", slotDetail.getUnitCode());
            assertEquals(LocalDate.of(2023, Month.NOVEMBER, 1), slotDetail.getBookingDate());
            assertEquals(LocalTime.of(10, 0), slotDetail.getBookingFromTime());
            assertEquals(LocalTime.of(12, 0), slotDetail.getBookingToTime());
            assertEquals("CONFIRMED", slotDetail.getStatus());
            assertEquals("100", slotDetail.getCapacity());
            assertNull(slotDetail.getAuditDetails());
        }
    }
}