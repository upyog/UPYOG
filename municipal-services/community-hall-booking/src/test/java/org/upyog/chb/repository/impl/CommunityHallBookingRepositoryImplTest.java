package org.upyog.chb.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.kafka.producer.Producer;
import org.upyog.chb.repository.querybuilder.CommunityHallBookingQueryBuilder;
import org.upyog.chb.repository.rowmapper.CommunityHallBookingRowmapper;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;

class CommunityHallBookingRepositoryImplTest {

    @Mock
    private Producer producer;

    @Mock
    private CommunityHallBookingConfiguration bookingConfiguration;

    @Mock
    private CommunityHallBookingQueryBuilder queryBuilder;

    @Mock
    private CommunityHallBookingRowmapper bookingRowmapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CommunityHallBookingRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCommunityHallBookingWithNullApplication() {
        // Arrange
        VenueBookingRequest bookingRequest = mock(VenueBookingRequest.class);
        when(bookingRequest.getVenueBookingApplication()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> repository.saveCommunityHallBooking(bookingRequest));
    }

    @Test
    void testGetBookingDetails() {
        // Arrange
        VenueBookingSearchCriteria criteria = new VenueBookingSearchCriteria();
        String query = "SELECT * FROM bookings";
        when(queryBuilder.getCommunityHallBookingSearchQuery(eq(criteria), any())).thenReturn(query);
        when(jdbcTemplate.query(eq(query), any(PreparedStatementSetter.class), eq(bookingRowmapper)))
                .thenReturn(new ArrayList<>());

        // Act
        List<VenueBookingDetail> result = repository.getBookingDetails(criteria);

        // Assert
        verify(jdbcTemplate).query(eq(query), any(PreparedStatementSetter.class), eq(bookingRowmapper));
        assertEquals(0, result.size());
    }

    @Test
    void testUpdateBookingWithNullApplication() {
        // Arrange
        VenueBookingRequest bookingRequest = mock(VenueBookingRequest.class);
        when(bookingRequest.getVenueBookingApplication()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> repository.updateBooking(bookingRequest));
    }
}