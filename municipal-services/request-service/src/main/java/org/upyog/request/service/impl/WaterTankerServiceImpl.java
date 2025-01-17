package org.upyog.request.service.impl;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.request.service.EnrichmentService;
import org.upyog.request.service.WaterTankerService;
import org.upyog.request.service.repository.RequestServiceRepository;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class WaterTankerServiceImpl implements WaterTankerService {
	
	
	@Autowired
	EnrichmentService enrichmentService;
	
	@Autowired
	RequestServiceRepository requestServiceRepository;

	@Override
	public WaterTankerBookingDetail createWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest) {

		RequestInfo requestInfo = waterTankerRequest.getRequestInfo();

		enrichmentService.enrichCreateWaterTankerRequest(waterTankerRequest);
		
		requestServiceRepository.saveWaterTankerBooking(waterTankerRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();

		return waterTankerDetail;
	}

}
