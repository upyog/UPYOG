package org.upyog.request.service.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.request.service.repository.rowMapper.GenericRowMapper;
import org.upyog.request.service.config.RequestServiceConfiguration;
import org.upyog.request.service.kafka.Producer;
import org.upyog.request.service.repository.RequestServiceRepository;
import org.upyog.request.service.repository.querybuilder.RequestServiceQueryBuilder;
import org.upyog.request.service.web.models.PersisterWrapper;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;
import org.upyog.request.service.web.models.WaterTankerBookingSearchCriteria;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class RequestServieRepositoryImpl implements RequestServiceRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private RequestServiceQueryBuilder queryBuilder;



	@Autowired
	private JdbcTemplate jdbcTemplate;

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


	@Override
	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(
			WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getWaterTankerQuery(waterTankerBookingSearchCriteria, preparedStmtList);
		log.info("Final query for getWaterTankerBookingDetails {} and paramsList {} : " , preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), new GenericRowMapper<>(WaterTankerBookingDetail.class));
	}



}
