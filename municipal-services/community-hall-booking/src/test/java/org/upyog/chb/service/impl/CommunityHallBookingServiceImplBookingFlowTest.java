package org.upyog.chb.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;

/**
 * Additional booking-service scenarios (Given / When / Then) with fully mocked infrastructure.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityHallBookingServiceImpl booking flow")
class CommunityHallBookingServiceImplBookingFlowTest {

	@InjectMocks
	private CommunityHallBookingServiceImpl bookingService;

	@Mock
	private CommunityHallBookingRepository bookingRepository;

	@Mock
	private CommunityHallBookingValidator hallBookingValidator;

	@Mock
	private WorkflowService workflowService;

	@Mock
	private EnrichmentService enrichmentService;

	@Mock
	private DemandService demandService;

	@Mock
	private MdmsUtil mdmsUtil;

	@Mock
	private CHBEncryptionService encryptionService;

	@Mock
	private BookingTimerService bookingTimerService;

	private RequestInfo citizenRequest;

	@BeforeEach
	void setUp() {
		var role = Role.builder().code(CommunityHallBookingConstants.CITIZEN).build();
		var user = User.builder().uuid("citizen-uuid-99").roles(List.of(role)).build();
		citizenRequest = RequestInfo.builder().userInfo(user).build();
	}

	@Test
	@DisplayName("Given valid tenant and user When createBooking Then demand is created and booking persisted")
	void createBooking_persistsAfterDemand() {
		var detail = VenueBookingDetail.builder().tenantId("pg.citya").bookingNo("CHB-001").build();
		var request = VenueBookingRequest.builder().requestInfo(citizenRequest).venueBookingApplication(detail)
				.build();

		// Production passes (requestInfo, tenantId). tenantId here is "pg.citya".
		when(mdmsUtil.mDMSCall(citizenRequest, "pg.citya")).thenReturn(new Object());

		lenient().doNothing().when(hallBookingValidator).validateCreate(any(), any(),any());
		lenient().doNothing().when(enrichmentService).enrichCreateBookingRequest(any());
		when(encryptionService.encryptObject(any(VenueBookingRequest.class)))
				.thenReturn(request.getVenueBookingApplication());
		when(demandService.createDemand(request, true)).thenReturn(Collections.emptyList());

		var result = bookingService.createBooking(request);

		assertThat(result.getBookingNo()).isEqualTo("CHB-001");
		verify(demandService).createDemand(request, true);
		verify(bookingRepository).saveCommunityHallBooking(request);
	}

	@Test
	@DisplayName("Given citizen role When getBookingDetails Then search criteria includes createdBy filter")
	void getBookingDetails_citizenScope() {
		var criteria = VenueBookingSearchCriteria.builder().bookingNo("CHB-1").build();
		lenient().doNothing().when(hallBookingValidator).validateSearch(eq(citizenRequest), any());
		when(bookingRepository.getBookingDetails(any(VenueBookingSearchCriteria.class)))
				.thenReturn(Collections.emptyList());

		bookingService.getBookingDetails(criteria, citizenRequest);

		var captor = ArgumentCaptor.forClass(VenueBookingSearchCriteria.class);
		verify(bookingRepository).getBookingDetails(captor.capture());
		assertThat(captor.getValue().getCreatedBy()).containsExactly("citizen-uuid-99");
	}
}
