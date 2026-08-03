package org.upyog.adv.web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.upyog.adv.service.AdvertisementValidationService;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.service.DemandService;
import org.upyog.adv.web.models.BookingDetail;

/**
 * Web layer tests for {@link AdvertisementServiceApiController}.
 */
@ExtendWith(MockitoExtension.class)
class AdvertisementServiceApiControllerTest {

	private static final String CREATE_BOOKING_URL = "/booking/v1/_create";
	private static final String TENANT_ID = "pb.amritsar";
	private static final String DRAFT_CREATE_REQUEST = """
			{
			  "isDraftApplication": true,
			  "RequestInfo": {
			    "apiId": "advertisement-service",
			    "ver": "1.0",
			    "action": "create"
			  },
			  "bookingApplication": {
			    "tenantId": "%s"
			  }
			}
			""".formatted(TENANT_ID);
	private static final String MALFORMED_JSON = "{invalid-json";

	private static final Validator NO_OP_VALIDATOR = new Validator() {
		@Override
		public boolean supports(Class<?> clazz) {
			return true;
		}

		@Override
		public void validate(Object target, Errors errors) {
			// Controller-layer validation is covered separately; these tests focus on handler wiring.
		}
	};

	@Mock
	private BookingService bookingService;

	@Mock
	private DemandService demandService;

	@Mock
	private AdvertisementValidationService validationService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		AdvertisementServiceApiController controller = new AdvertisementServiceApiController(validationService,
				bookingService, demandService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(NO_OP_VALIDATOR).build();
	}

	@Test
	void advertisementServiceCreatePostSuccess() throws Exception {
		BookingDetail bookingDetail = BookingDetail.builder().draftId("draft-001").tenantId(TENANT_ID).build();
		doNothing().when(validationService).validateRequest(any());
		when(bookingService.createAdvertisementDraftApplication(any())).thenReturn(bookingDetail);

		mockMvc.perform(post(CREATE_BOOKING_URL).contentType(MediaType.APPLICATION_JSON)
				.content(DRAFT_CREATE_REQUEST)).andExpect(status().isOk())
				.andExpect(jsonPath("$.bookingApplication[0].draftId").value("draft-001"));

		verify(validationService).validateRequest(any());
		verify(bookingService).createAdvertisementDraftApplication(any());
	}

	@Test
	void advertisementServiceCreatePostFailure() throws Exception {
		mockMvc.perform(post(CREATE_BOOKING_URL).contentType(MediaType.APPLICATION_JSON).content(MALFORMED_JSON))
				.andExpect(status().isBadRequest());
	}

}
