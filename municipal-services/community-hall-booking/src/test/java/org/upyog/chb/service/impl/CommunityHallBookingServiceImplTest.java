package org.upyog.chb.service.impl;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.BookingTimerService;
import org.upyog.chb.service.CHBEncryptionService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityHallBookingServiceImplTest {

    @InjectMocks
    private CommunityHallBookingServiceImpl communityHallBookingService;

    @Mock
    private CommunityHallBookingRepository bookingRepository;

    @Mock
    private CommunityHallBookingValidator hallBookingValidator;

    @Mock
    private EnrichmentService enrichmentService;

    @Mock
    private CHBEncryptionService encryptionService;

    @Mock
    private MdmsUtil mdmsUtil;

    @Mock
    private DemandService demandService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private BookingTimerService bookingTimerService;

    private RequestInfo requestInfo;

    @BeforeEach
    void setUp() {
        var user = User.builder()
                .uuid("test-user-uuid")
                .roles(new ArrayList<>()) // Initialize roles with an empty list
                .build();
        requestInfo = RequestInfo.builder().userInfo(user).build();
    }

    @Test
    @DisplayName("Should create booking successfully")
    void createBooking_Success() {
        VenueBookingRequest request = VenueBookingRequest.builder()
                .requestInfo(requestInfo)
                .venueBookingApplication(VenueBookingDetail.builder()
                        .tenantId("tenant.001").venueType("PARKS")
                        .build())
                .build();

        // Mocking dependencies
        when(mdmsUtil.mDMSCall(any(RequestInfo.class), anyString())).thenReturn(new Object());
        lenient().doNothing().when(hallBookingValidator)
                .validateCreate(any(VenueBookingRequest.class), any(), eq("PARKS"));
        lenient().doNothing().when(enrichmentService).enrichCreateBookingRequest(any(VenueBookingRequest.class));

        lenient().when(encryptionService.encryptObject(any(VenueBookingRequest.class)))
                .thenReturn(request.getVenueBookingApplication());
        lenient().when(demandService.createDemand(eq(request), eq(true))).thenReturn(Collections.emptyList());

        lenient().doAnswer(invocation -> {
            VenueBookingRequest req = invocation.getArgument(0);
            req.getVenueBookingApplication().setBookingNo("BOOKING-123");
            return null;
        }).when(bookingRepository).saveCommunityHallBooking(any(VenueBookingRequest.class));

        VenueBookingDetail result = communityHallBookingService.createBooking(request);

        assertNotNull(result);
        assertEquals("BOOKING-123", result.getBookingNo());
    }

    @Test
    @DisplayName("Should throw CustomException for invalid tenant ID")
    void createBooking_InvalidTenantId() {
        VenueBookingRequest request = VenueBookingRequest.builder()
                .requestInfo(requestInfo)
                .venueBookingApplication(VenueBookingDetail.builder()
                        .tenantId("tenant") // Invalid tenant ID format
                        .build())
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> communityHallBookingService.createBooking(request));
        assertEquals(CommunityHallBookingConstants.INVALID_TENANT, exception.getCode());
    }

    @Test
    @DisplayName("Should get booking details successfully")
    void getBookingDetails_Success() {
        VenueBookingSearchCriteria criteria = VenueBookingSearchCriteria.builder()
                .bookingNo("BOOKING-123")
                .build();

        List<VenueBookingDetail> expectedBookings = new ArrayList<>();
        expectedBookings.add(VenueBookingDetail.builder()
                .bookingNo("BOOKING-123")
                .applicantDetail(ApplicantDetail.builder().applicantMobileNo("1234567890").build())
                .build());

        // Mocking dependencies
        lenient().doNothing().when(hallBookingValidator).validateSearch(any(RequestInfo.class), any(VenueBookingSearchCriteria.class));
        when(bookingRepository.getBookingDetails(any(VenueBookingSearchCriteria.class))).thenReturn(expectedBookings);
        // The original error was here. The decryptObject method is expected to return a List of CommunityHallBookingDetail.
        // The method signature for decryptObject in CHBEncryptionService likely takes an object to decrypt and returns a T.
        // Here, we are decrypting a List of objects, and it's expected to return a List of CommunityHallBookingDetail.
        lenient().when(encryptionService.decryptObject(any(List.class), any(RequestInfo.class))).thenReturn(expectedBookings);

        List<VenueBookingDetail> result = communityHallBookingService.getBookingDetails(criteria, requestInfo);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BOOKING-123", result.get(0).getBookingNo());
    }

    @Test
    @DisplayName("Should return empty list when no bookings found")
    void getBookingDetails_NoBookingsFound() {
        VenueBookingSearchCriteria criteria = VenueBookingSearchCriteria.builder()
                .bookingNo("NONEXISTENT-BOOKING")
                .build();

        // Mocking dependencies
        lenient().doNothing().when(hallBookingValidator).validateSearch(any(RequestInfo.class), any(VenueBookingSearchCriteria.class));
        when(bookingRepository.getBookingDetails(any(VenueBookingSearchCriteria.class))).thenReturn(Collections.emptyList());
        lenient().when(encryptionService.decryptObject(any(List.class), any(RequestInfo.class))).thenReturn(Collections.emptyList());

        List<VenueBookingDetail> result = communityHallBookingService.getBookingDetails(criteria, requestInfo);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get booking count successfully")
    void getBookingCount_Success() {
        VenueBookingSearchCriteria criteria = VenueBookingSearchCriteria.builder()
                .tenantId("tenant.001")
                .build();
        Integer expectedCount = 5;

        // Mocking dependencies
        when(bookingRepository.getBookingCount(any(VenueBookingSearchCriteria.class))).thenReturn(expectedCount);

        Integer result = communityHallBookingService.getBookingCount(criteria, requestInfo);

        assertNotNull(result);
        assertEquals(expectedCount, result);
    }

    @Test
    @DisplayName("Should update booking successfully")
    void updateBooking_Success() {
        VenueBookingDetail existingBooking = VenueBookingDetail.builder()
                .bookingId("booking-id-1")
                .bookingNo("BOOKING-123")
                .build();
        VenueBookingRequest request = VenueBookingRequest.builder()
                .requestInfo(requestInfo)
                .venueBookingApplication(existingBooking)
                .build();

        // Mocking dependencies
        List<VenueBookingDetail> bookingDetails = new ArrayList<>();
        bookingDetails.add(existingBooking);
        when(bookingRepository.getBookingDetails(any(VenueBookingSearchCriteria.class))).thenReturn(bookingDetails);
        lenient().doNothing().when(hallBookingValidator).validateUpdate(any(VenueBookingDetail.class), any(VenueBookingDetail.class));
        lenient().doNothing().when(enrichmentService).enrichUpdateBookingRequest(any(VenueBookingRequest.class), any());
        lenient().doNothing().when(bookingRepository).updateBooking(any(VenueBookingRequest.class));

        VenueBookingDetail result = communityHallBookingService.updateBooking(request, null, null);

        assertNotNull(result);
        assertEquals("BOOKING-123", result.getBookingNo());
    }

    @Test
    @DisplayName("Should throw CustomException for invalid booking number on update")
    void updateBooking_InvalidBookingNumber() {
        VenueBookingRequest request = VenueBookingRequest.builder()
                .requestInfo(requestInfo)
                .venueBookingApplication(VenueBookingDetail.builder()
                        .bookingNo(null) // Invalid booking number
                        .build())
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> communityHallBookingService.updateBooking(request, null, null));
        assertEquals("INVALID_BOOKING_CODE", exception.getCode());
    }

    @Test
    @DisplayName("Should throw CustomException if booking not found on update")
    void updateBooking_BookingNotFound() {
        VenueBookingRequest request = VenueBookingRequest.builder()
                .requestInfo(requestInfo)
                .venueBookingApplication(VenueBookingDetail.builder()
                        .bookingNo("NONEXISTENT-BOOKING")
                        .build())
                .build();

        // Use ArgumentMatchers to avoid strict stubbing issues
        when(bookingRepository.getBookingDetails(any(VenueBookingSearchCriteria.class)))
                .thenReturn(Collections.emptyList());

        CustomException exception = assertThrows(CustomException.class, () -> communityHallBookingService.updateBooking(request, null, null));
        assertEquals("INVALID_BOOKING_CODE", exception.getCode());
    }

    @Test
    @DisplayName("Should get community hall slot availability and set fromTime and toTime correctly from database slot details")
    void getCommunityHallSlotAvailability_SetsTimesFromDB() {
        org.upyog.chb.web.models.VenueSlotSearchCriteria criteria = org.upyog.chb.web.models.VenueSlotSearchCriteria.builder()
                .tenantId("tenant.001")
                .venueCode("VENUE-001")
                .unitCode("HALL-001")
                .bookingStartDate("2026-06-18")
                .bookingEndDate("2026-06-18")
                .fromTime("09:00")
                .toTime("18:00")
                .isTimerRequired(false)
                .build();

        List<org.upyog.chb.web.models.VenueSlotAvailabilityDetail> dbSlots = new ArrayList<>();
        dbSlots.add(org.upyog.chb.web.models.VenueSlotAvailabilityDetail.builder()
                .tenantId("tenant.001")
                .venueCode("VENUE-001")
                .code("HALL-001")
                .bookingDate("18-06-2026")
                .fromTime("10:00")
                .toTime("17:00")
                .slotStaus("BOOKED")
                .build());

        when(bookingRepository.getCommunityHallSlotAvailability(any(org.upyog.chb.web.models.VenueSlotSearchCriteria.class)))
                .thenReturn(dbSlots);

        org.upyog.chb.web.models.VenueSlotAvailabilityResponse response = communityHallBookingService.getCommunityHallSlotAvailability(criteria, requestInfo);

        assertNotNull(response);
        assertNotNull(response.getHallSlotAvailabiltityDetails());
        assertEquals(1, response.getHallSlotAvailabiltityDetails().size());
        org.upyog.chb.web.models.VenueSlotAvailabilityDetail detail = response.getHallSlotAvailabiltityDetails().get(0);
        assertEquals("BOOKED", detail.getSlotStaus());
        assertEquals("10:00", detail.getFromTime());
        assertEquals("17:00", detail.getToTime());
    }
}