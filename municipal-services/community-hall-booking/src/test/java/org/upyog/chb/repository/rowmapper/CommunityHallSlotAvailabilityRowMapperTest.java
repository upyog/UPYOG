package org.upyog.chb.repository.rowmapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.VenueSlotAvailabilityDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunityHallSlotAvailabilityRowMapperTest {

    @Mock
    private ResultSet resultSet;

    private CommunityHallSlotAvailabilityRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowMapper = new CommunityHallSlotAvailabilityRowMapper();
    }

    @Test
    void testExtractData() throws SQLException, DataAccessException {
        // Arrange
        when(resultSet.next()).thenReturn(true, false); // Simulate one row
        when(resultSet.getString("booking_date")).thenReturn("2023-11-01");
        when(resultSet.getString("venue_code")).thenReturn("CH001");
        when(resultSet.getString("unit_code")).thenReturn("H001");
        when(resultSet.getString("status")).thenReturn("AVAILABLE");
        when(resultSet.getString("tenant_id")).thenReturn("T001");

        try (var mockedUtil = mockStatic(CommunityHallBookingUtil.class)) {
            mockedUtil.when(() -> CommunityHallBookingUtil.convertDateFormat("2023-11-01", CommunityHallBookingConstants.DATE_FORMAT))
                    .thenReturn("01-11-2023");

            // Act
            List<VenueSlotAvailabilityDetail> result = rowMapper.extractData(resultSet);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            VenueSlotAvailabilityDetail detail = result.get(0);
            assertEquals("01-11-2023", detail.getBookingDate());
            assertEquals("CH001", detail.getVenueCode());
            assertEquals("H001", detail.getCode());
            assertEquals("AVAILABLE", detail.getSlotStaus());
            assertEquals("T001", detail.getTenantId());
        }
    }
}