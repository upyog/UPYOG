package org.upyog.sv.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.service.PaymentService;
import org.upyog.sv.service.SchedulerService;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.validator.StreetVendingValidationService;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingListResponse;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingResponse;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.common.RequestInfoWrapper;
import org.upyog.sv.web.models.common.ResponseInfo;
import org.upyog.sv.web.models.common.ResponseInfo.StatusEnum;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-16T13:19:19.125+05:30")

@Controller
@RequestMapping("/street-vending")
public class StreetVendingController {

	@Autowired
	private StreetVendingService streetVendingService;

	@Autowired
	private SchedulerService schedulerService;

	private final StreetVendingValidationService validationService;
	
	 @Autowired
	    private PaymentService paymentService;

	public StreetVendingController(StreetVendingValidationService validationService) {
		this.validationService = validationService;
	}

	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<StreetVendingResponse> createStreetVendingApplication(
			@RequestBody StreetVendingRequest vendingRequest) {

		StreetVendingDetail streetVendingDetail = null;
		validationService.validateRequest(vendingRequest); /// To validate the Create application request
		if (vendingRequest.isDraftApplication()) {
			streetVendingDetail = streetVendingService.createStreetVendingDraftApplication(vendingRequest);
		} else {
			streetVendingDetail = streetVendingService.createStreetVendingApplication(vendingRequest);
		}

		StreetVendingResponse response = StreetVendingResponse.builder().streetVendingDetail(streetVendingDetail)
				.responseInfo(StreetVendingUtil.createReponseInfo(vendingRequest.getRequestInfo(),
						StreetVendingConstants.APPLICATION_CREATED, StatusEnum.SUCCESSFUL))
				.build();

		return new ResponseEntity<StreetVendingResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<StreetVendingListResponse> streetVendingSearch(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<StreetVendingDetail> applications = null;
		Integer count = 0;
		if ("true".equals(streetVendingSearchCriteria.getIsDraftApplication())) {
			applications = streetVendingService.getStreetVendingDraftApplicationDetails(
					requestInfoWrapper.getRequestInfo(), streetVendingSearchCriteria);
			count = applications != null ? applications.size() : 0;

		} else {

			applications = streetVendingService.getStreetVendingDetails(requestInfoWrapper.getRequestInfo(),
					streetVendingSearchCriteria);
			count = streetVendingService.getApplicationsCount(streetVendingSearchCriteria,
					requestInfoWrapper.getRequestInfo());
		}

		ResponseInfo responseInfo = StreetVendingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(),
				StreetVendingConstants.APPLICATIONS_FOUND, StatusEnum.SUCCESSFUL);
		StreetVendingListResponse response = StreetVendingListResponse.builder().streetVendingDetail(applications)
				.responseInfo(responseInfo).count(count).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<StreetVendingResponse> streetVendingUpdate(
			@ApiParam(value = "Updated Street vending details and RequestInfo meta data.", required = true) @RequestBody StreetVendingRequest vendingRequest) {
		validationService.validateRequest(vendingRequest); /// To validate the Update application request
		StreetVendingDetail streetVendingDetail = streetVendingService.updateStreetVendingApplication(vendingRequest);

		StreetVendingResponse response = StreetVendingResponse.builder().streetVendingDetail(streetVendingDetail)
				.responseInfo(StreetVendingUtil.createReponseInfo(vendingRequest.getRequestInfo(),
						StreetVendingConstants.APPLICATION_UPDATED, StatusEnum.SUCCESSFUL))
				.build();
		return new ResponseEntity<StreetVendingResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/_deletedraft", method = RequestMethod.POST)
	public ResponseEntity<StreetVendingResponse> streetVendingDeleteDraft(
			@ApiParam(value = "Details for draft deletion and RequestInfo meta data.", required = true) @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "draftId", required = true) String draftId) {
		String draftDiscardResponse = streetVendingService.deleteStreetVendingDraft(draftId);
		ResponseInfo responseInfo = StreetVendingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(),
				draftDiscardResponse, StatusEnum.SUCCESSFUL);
		StreetVendingResponse response = StreetVendingResponse.builder().responseInfo(responseInfo).build();
		return new ResponseEntity<StreetVendingResponse>(response, HttpStatus.OK);
	}

//	@PostMapping("/_createdemand")
//	public ResponseEntity<StreetVendingDemandResponse> createDemandForRenewal(
//			@ApiParam(value = "Details for demand create and RequestInfo meta data.", required = true) @RequestBody StreetVendingRequest vendingRequest) {
//		List<Demand> demands = streetVendingService.demandCreation(vendingRequest);
//		ResponseInfo responseInfo = StreetVendingUtil.createReponseInfo(vendingRequest.getRequestInfo(),
//				StreetVendingConstants.RENEWAL_DEMAND_CREATED, StatusEnum.SUCCESSFUL);
//		StreetVendingDemandResponse response = StreetVendingDemandResponse.builder().responseInfo(responseInfo)
//				.demands(demands).build();
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@RequestMapping(value = "/trigger-expire-streetvendingapplications", method = RequestMethod.POST)
	public ResponseEntity<String> triggerWorkflowUpdate() {
		try {
			schedulerService.processStreetVendingApplications();
			return ResponseEntity.ok("Expire Scheduler triggered successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to trigger scheduler: " + e.getMessage());
		}
	}

	@PostMapping("/trigger-payment-schedule")
    public ResponseEntity<String> triggerPaymentSchedule() {
        try {
        	paymentService.processDueVendorPayments(null); 
            return ResponseEntity.ok("Payment Scheduler triggered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to trigger scheduler: " + e.getMessage());
        }
    }
	
}
