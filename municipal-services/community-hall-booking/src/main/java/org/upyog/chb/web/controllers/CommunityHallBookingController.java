package org.upyog.chb.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.CommunityHallBookingResponse;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Controller
@RequestMapping("/booking")
public class CommunityHallBookingController {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	public CommunityHallBookingController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}
	
	@Autowired
	private CommunityHallBookingService bookingService;

	@RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> createBooking(
			@ApiParam(value = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<CommunityHallBookingResponse>(objectMapper.readValue(
						"{  \"headerImageUrl\" : \"headerImageUrl\",  \"specialCategories\" : [ {    \"discountRate\" : 7,    \"category\" : \"category\"  }, {    \"discountRate\" : 7,    \"category\" : \"category\"  } ],  \"address\" : {    \"pincode\" : \"pincode\",    \"city\" : \"city\",    \"latitude\" : 6.027456183070403,    \"locality\" : {      \"code\" : \"code\",      \"materializedPath\" : \"materializedPath\",      \"children\" : [ null, null ],      \"latitude\" : \"latitude\",      \"name\" : \"name\",      \"label\" : \"label\",      \"longitude\" : \"longitude\"    },    \"type\" : \"type\",    \"addressId\" : \"addressId\",    \"buildingName\" : \"buildingName\",    \"street\" : \"street\",    \"tenantId\" : \"tenantId\",    \"addressNumber\" : \"addressNumber\",    \"addressLine1\" : \"addressLine1\",    \"addressLine2\" : \"addressLine2\",    \"doorNo\" : \"doorNo\",    \"detail\" : \"detail\",    \"landmark\" : \"landmark\",    \"longitude\" : 1.4658129805029452  },  \"hallDescription\" : \"hallDescription\",  \"timeSlots\" : [ {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  }, {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  } ],  \"type\" : \"type\",  \"contactDetails\" : \"contactDetails\",  \"hallCode\" : 0,  \"geoLocation\" : \"geoLocation\",  \"maxAllowedFutureBookingDays\" : 1,  \"purposes\" : [ {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  }, {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  } ],  \"portalUrl\" : \"portalUrl\",  \"auditDetails\" : {    \"lastModifiedTime\" : 5,    \"createdBy\" : \"createdBy\",    \"lastModifiedBy\" : \"lastModifiedBy\",    \"createdTime\" : 4  },  \"tenantId\" : \"tenantId\",  \"name\" : \"name\",  \"id\" : \"id\",  \"termsAndCondition\" : \"termsAndCondition\",  \"penantlyForWrongDocuments\" : 1,  \"residentTypes\" : [ {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  }, {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  } ],  \"cancellationPolicy\" : [ {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  }, {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  } ],  \"remarks\" : \"remarks\"}",
						CommunityHallBookingResponse.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				return new ResponseEntity<CommunityHallBookingResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		CommunityHallBookingDetail bookingDetail = bookingService.createBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder()
				.responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v1/_init", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> initBooking(
			@ApiParam(value = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<CommunityHallBookingResponse>(objectMapper.readValue(
						"{  \"headerImageUrl\" : \"headerImageUrl\",  \"specialCategories\" : [ {    \"discountRate\" : 7,    \"category\" : \"category\"  }, {    \"discountRate\" : 7,    \"category\" : \"category\"  } ],  \"address\" : {    \"pincode\" : \"pincode\",    \"city\" : \"city\",    \"latitude\" : 6.027456183070403,    \"locality\" : {      \"code\" : \"code\",      \"materializedPath\" : \"materializedPath\",      \"children\" : [ null, null ],      \"latitude\" : \"latitude\",      \"name\" : \"name\",      \"label\" : \"label\",      \"longitude\" : \"longitude\"    },    \"type\" : \"type\",    \"addressId\" : \"addressId\",    \"buildingName\" : \"buildingName\",    \"street\" : \"street\",    \"tenantId\" : \"tenantId\",    \"addressNumber\" : \"addressNumber\",    \"addressLine1\" : \"addressLine1\",    \"addressLine2\" : \"addressLine2\",    \"doorNo\" : \"doorNo\",    \"detail\" : \"detail\",    \"landmark\" : \"landmark\",    \"longitude\" : 1.4658129805029452  },  \"hallDescription\" : \"hallDescription\",  \"timeSlots\" : [ {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  }, {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  } ],  \"type\" : \"type\",  \"contactDetails\" : \"contactDetails\",  \"hallCode\" : 0,  \"geoLocation\" : \"geoLocation\",  \"maxAllowedFutureBookingDays\" : 1,  \"purposes\" : [ {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  }, {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  } ],  \"portalUrl\" : \"portalUrl\",  \"auditDetails\" : {    \"lastModifiedTime\" : 5,    \"createdBy\" : \"createdBy\",    \"lastModifiedBy\" : \"lastModifiedBy\",    \"createdTime\" : 4  },  \"tenantId\" : \"tenantId\",  \"name\" : \"name\",  \"id\" : \"id\",  \"termsAndCondition\" : \"termsAndCondition\",  \"penantlyForWrongDocuments\" : 1,  \"residentTypes\" : [ {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  }, {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  } ],  \"cancellationPolicy\" : [ {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  }, {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  } ],  \"remarks\" : \"remarks\"}",
						CommunityHallBookingResponse.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				return new ResponseEntity<CommunityHallBookingResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		CommunityHallBookingDetail bookingDetail = bookingService.createInitBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder().responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1RegistrationUpdatePost(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<CommunityHallBookingResponse>(objectMapper.readValue(
						"{  \"headerImageUrl\" : \"headerImageUrl\",  \"specialCategories\" : [ {    \"discountRate\" : 7,    \"category\" : \"category\"  }, {    \"discountRate\" : 7,    \"category\" : \"category\"  } ],  \"address\" : {    \"pincode\" : \"pincode\",    \"city\" : \"city\",    \"latitude\" : 6.027456183070403,    \"locality\" : {      \"code\" : \"code\",      \"materializedPath\" : \"materializedPath\",      \"children\" : [ null, null ],      \"latitude\" : \"latitude\",      \"name\" : \"name\",      \"label\" : \"label\",      \"longitude\" : \"longitude\"    },    \"type\" : \"type\",    \"addressId\" : \"addressId\",    \"buildingName\" : \"buildingName\",    \"street\" : \"street\",    \"tenantId\" : \"tenantId\",    \"addressNumber\" : \"addressNumber\",    \"addressLine1\" : \"addressLine1\",    \"addressLine2\" : \"addressLine2\",    \"doorNo\" : \"doorNo\",    \"detail\" : \"detail\",    \"landmark\" : \"landmark\",    \"longitude\" : 1.4658129805029452  },  \"hallDescription\" : \"hallDescription\",  \"timeSlots\" : [ {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  }, {    \"electricityCharges\" : 7,    \"waterCharges\" : 9,    \"conservancyCharges\" : 3,    \"from\" : \"from\",    \"securityDeposit\" : 2,    \"id\" : 5,    \"to\" : \"to\",    \"rent\" : 5  } ],  \"type\" : \"type\",  \"contactDetails\" : \"contactDetails\",  \"hallCode\" : 0,  \"geoLocation\" : \"geoLocation\",  \"maxAllowedFutureBookingDays\" : 1,  \"purposes\" : [ {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  }, {    \"discountRate\" : 4,    \"purpose\" : \"purpose\"  } ],  \"portalUrl\" : \"portalUrl\",  \"auditDetails\" : {    \"lastModifiedTime\" : 5,    \"createdBy\" : \"createdBy\",    \"lastModifiedBy\" : \"lastModifiedBy\",    \"createdTime\" : 4  },  \"tenantId\" : \"tenantId\",  \"name\" : \"name\",  \"id\" : \"id\",  \"termsAndCondition\" : \"termsAndCondition\",  \"penantlyForWrongDocuments\" : 1,  \"residentTypes\" : [ {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  }, {    \"discountRate\" : 2,    \"disabled\" : true,    \"type\" : \"type\"  } ],  \"cancellationPolicy\" : [ {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  }, {    \"id\" : 1,    \"cancelTo\" : 7,    \"cancelFrom\" : 6,    \"percentageDeduction\" : 1  } ],  \"remarks\" : \"remarks\"}",
						CommunityHallBookingResponse.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				return new ResponseEntity<CommunityHallBookingResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<CommunityHallBookingResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1SearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute CommunityHallBookingSearchCriteria criteria) {
		List<CommunityHallBookingDetail> applications = bookingService.getBookingDetails(criteria);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_LIST,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse response = CommunityHallBookingResponse.builder().hallsBookingApplication(applications)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
