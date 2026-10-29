package org.upyog.chb.repository.querybuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunityHallBookingQueryBuilderTest {

    @Mock
    private CommunityHallBookingConfiguration bookingConfiguration;

    @InjectMocks
    private CommunityHallBookingQueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCommunityHallBookingSearchQuery() {
        VenueBookingSearchCriteria criteria = createSearchCriteria("pg.test-tenant", "12345", "PENDING");
        List<Object> preparedStmtList = new ArrayList<>();

        String query = queryBuilder.getCommunityHallBookingSearchQuery(criteria, preparedStmtList);

        assertNotNull(query);
        assertTrue(query.contains("ecbd.tenant_id=?"));
        assertTrue(query.contains("ecbd.booking_no IN"));
        assertTrue(query.contains("ecbd.booking_status ="));
        //+2 for offset and limit
        assertEquals(5, preparedStmtList.size());
    }

    @Test
    void testGetSlotDetailsQuery() {
        List<String> bookingIds = Arrays.asList("id1", "id2");

        String query = queryBuilder.getSlotDetailsQuery(bookingIds);

        assertNotNull(query);
        assertTrue(query.contains("booking_id in"));
        assertTrue(query.contains("?"));
    }

    @Test
    void testGetDocumentDetailsQuery() {
        List<String> bookingIds = Arrays.asList("id1", "id2");

        String query = queryBuilder.getDocumentDetailsQuery(bookingIds);

        assertNotNull(query);
        assertTrue(query.contains("booking_id in"));
        assertTrue(query.contains("?"));
    }

    @Test
    void testGetCommunityHallSlotAvailabilityQuery() {
        VenueSlotSearchCriteria criteria = createSlotSearchCriteria("test-tenant", "hall-code", "2023-01-01", "2023-01-02");
        List<Object> paramsList = new ArrayList<>();

        StringBuilder query = queryBuilder.getCommunityHallSlotAvailabilityQuery(criteria, paramsList);

        assertNotNull(query);
        assertTrue(query.toString().contains("ecbd.tenant_id= ?"));
        assertTrue(query.toString().contains("ecbd.venue_code = ?"));
        assertEquals(4, paramsList.size());
    }

    @Test
    void testGetCommunityHallSlotAvailabilityQueryWithStartEndTime() {
        VenueSlotSearchCriteria criteria = createSlotSearchCriteria("test-tenant", "hall-code", "2023-01-01", "2023-01-02");
        criteria.setFromTime("09:00");
        criteria.setToTime("12:00");
        List<Object> paramsList = new ArrayList<>();

        StringBuilder query = queryBuilder.getCommunityHallSlotAvailabilityQuery(criteria, paramsList);

        assertNotNull(query);
        assertTrue(query.toString().contains("ecsd.booking_to_time >= CAST(? AS TIME)"));
        assertTrue(query.toString().contains("ecsd.booking_from_time <= CAST(? AS TIME)"));
        assertEquals(6, paramsList.size());
    }

    @Test
    void testAddPaginationWrapper() throws Exception {
        // Arrange
        VenueBookingSearchCriteria criteria = createPaginationCriteria(10, 5);
        List<Object> preparedStmtList = new ArrayList<>();
        String query = "SELECT * FROM bookings";

        when(bookingConfiguration.getDefaultLimit()).thenReturn(10);
        when(bookingConfiguration.getDefaultOffset()).thenReturn(0);
        when(bookingConfiguration.getMaxSearchLimit()).thenReturn(100);

        // Access private method using reflection
        Method method = CommunityHallBookingQueryBuilder.class.getDeclaredMethod(
                "addPaginationWrapper", String.class, List.class, VenueBookingSearchCriteria.class);
        method.setAccessible(true);

        // Act
        String paginatedQuery = (String) method.invoke(queryBuilder, query, preparedStmtList, criteria);

        // Assert
        assertNotNull(paginatedQuery);
        assertTrue(paginatedQuery.contains("WHERE offset_ > ? AND offset_ <= ?"));
        assertEquals(2, preparedStmtList.size());
    }

    @Test
    void testCreateQueryParams() throws Exception {
        // Arrange
        List<String> ids = Arrays.asList("id1", "id2", "id3");

        // Access private method using reflection
        Method method = CommunityHallBookingQueryBuilder.class.getDeclaredMethod(
                "createQueryParams", List.class);
        method.setAccessible(true);

        // Act
        Object queryParams = method.invoke(queryBuilder, ids);

        // Assert
        assertNotNull(queryParams);
        assertEquals(" ?, ?, ?", queryParams.toString());
    }

    @Test
    void testAddClauseIfRequired() throws Exception {
        // Arrange
        List<Object> values = new ArrayList<>();
        StringBuilder queryString = new StringBuilder("SELECT * FROM bookings");

        // Access private method using reflection
        Method method = CommunityHallBookingQueryBuilder.class.getDeclaredMethod(
                "addClauseIfRequired", List.class, StringBuilder.class);
        method.setAccessible(true);

        // Act
        method.invoke(queryBuilder, values, queryString);

        // Assert
        assertTrue(queryString.toString().contains("WHERE"));
    }

    @Test
    void testAddToPreparedStatement() throws Exception {
        // Arrange
        List<Object> preparedStmtList = new ArrayList<>();
        List<String> ids = Arrays.asList("id1", "id2");

        // Access private method using reflection
        Method method = CommunityHallBookingQueryBuilder.class.getDeclaredMethod(
                "addToPreparedStatement", List.class, List.class);
        method.setAccessible(true);

        // Act
        method.invoke(queryBuilder, preparedStmtList, ids);

        // Assert
        assertEquals(2, preparedStmtList.size());
    }

    private VenueBookingSearchCriteria createSearchCriteria(String tenantId, String bookingNo, String status) {
        VenueBookingSearchCriteria criteria = new VenueBookingSearchCriteria();
        criteria.setTenantId(tenantId);
        criteria.setBookingNo(bookingNo);
        criteria.setStatus(status);
        return criteria;
    }

    private VenueSlotSearchCriteria createSlotSearchCriteria(String tenantId, String hallCode, String startDate, String endDate) {
        VenueSlotSearchCriteria criteria = new VenueSlotSearchCriteria();
        criteria.setTenantId(tenantId);
        criteria.setVenueCode(hallCode);
        criteria.setBookingStartDate(startDate);
        criteria.setBookingEndDate(endDate);
        return criteria;
    }

    private VenueBookingSearchCriteria createPaginationCriteria(int limit, int offset) {
        VenueBookingSearchCriteria criteria = new VenueBookingSearchCriteria();
        criteria.setLimit(limit);
        criteria.setOffset(offset);
        return criteria;
    }
}