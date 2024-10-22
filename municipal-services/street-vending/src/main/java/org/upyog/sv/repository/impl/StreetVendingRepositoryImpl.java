package org.upyog.sv.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.kafka.Producer;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.web.models.StreetVendingRequest;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class StreetVendingRepositoryImpl implements StreetVendingRepository{

	@Autowired
	private Producer producer;
	@Autowired
	private StreetVendingConfiguration vendingConfiguration;
	
	@Override
	public void save(StreetVendingRequest streetVendingRequest) {
		log.info("Saving community hall booking request data for booking no : " + streetVendingRequest.getStreetVendingDetail().getApplicationNo());
		producer.push(vendingConfiguration.getStreetVendingApplicationSaveTopic(), streetVendingRequest);
	}
	
	

}
