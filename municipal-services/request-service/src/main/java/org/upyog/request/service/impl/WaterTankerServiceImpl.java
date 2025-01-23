package org.upyog.request.service.impl;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.request.service.EnrichmentService;
import org.upyog.request.service.WaterTankerService;
import org.upyog.request.service.repository.RequestServiceRepository;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;
import org.upyog.request.service.web.models.WaterTankerBookingSearchCriteria;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class WaterTankerServiceImpl implements WaterTankerService {
	
	@Autowired
	EnrichmentService enrichmentService;
	
	@Autowired
	RequestServiceRepository requestServiceRepository;

	@Override
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest) {

		log.info("Create water tanker booking for user : " + waterTankerRequest.getRequestInfo().getUserInfo().getUuid()
				+ " for the request : " + waterTankerRequest.getWaterTankerBookingDetail());

		enrichmentService.enrichCreateWaterTankerRequest(waterTankerRequest);

		requestServiceRepository.saveWaterTankerBooking(waterTankerRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();

		return waterTankerDetail;
	}

	@Override
	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo,
															 WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {


		List<WaterTankerBookingDetail> applications = requestServiceRepository
				.getWaterTankerBookingDetails(waterTankerBookingSearchCriteria);
		if (CollectionUtils.isEmpty(applications)) {
			return new ArrayList<>();
		}
		return applications;
	}

//	@Override
//	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria,
//										RequestInfo requestInfo) {
//		waterTankerBookingSearchCriteria.setCountCall(true);
//		Integer bookingCount = 0;
//
//		waterTankerBookingSearchCriteria = addCreatedByMeToCriteria(waterTankerBookingSearchCriteria, requestInfo);
//		bookingCount = requestServiceRepository.getApplicationsCount(waterTankerBookingSearchCriteria);
//
//		return bookingCount;
//	}

}
