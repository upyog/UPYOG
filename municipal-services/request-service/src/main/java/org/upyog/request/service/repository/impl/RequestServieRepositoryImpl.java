package org.upyog.request.service.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.request.service.config.RequestServiceConfiguration;
import org.upyog.request.service.kafka.Producer;
import org.upyog.request.service.repository.RequestServiceRepository;
import org.upyog.request.service.web.models.PersisterWrapper;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RequestServieRepositoryImpl implements RequestServiceRepository {

	@Autowired
	private Producer producer;

	@Autowired
	RequestServiceConfiguration requestServiceConfiguration;

	@Override
	public void saveWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest) {
		log.info("Saving water tanker booking request data for booking no : "
				+ waterTankerRequest.getWaterTankerBookingDetail().getBookingNo());
		WaterTankerBookingDetail waterTankerBookingDetail = waterTankerRequest.getWaterTankerBookingDetail();
		PersisterWrapper<WaterTankerBookingDetail> persisterWrapper = new PersisterWrapper<WaterTankerBookingDetail>(
				waterTankerBookingDetail);
		producer.push(requestServiceConfiguration.getWaterTankerApplicationSaveTopic(), persisterWrapper);
	}

}
