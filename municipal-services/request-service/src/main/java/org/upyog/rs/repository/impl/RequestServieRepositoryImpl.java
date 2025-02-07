package org.upyog.rs.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.kafka.Producer;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.repository.querybuilder.RequestServiceQueryBuilder;
import org.upyog.rs.repository.rowMapper.GenericRowMapper;
import org.upyog.rs.web.models.PersisterWrapper;
import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.WaterTankerBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;


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
		producer.push(requestServiceConfiguration.getWaterTankerApplicationSaveTopic(), waterTankerRequest);
	}


	@Override
	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(
			WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {
		//create a list to hold the statement parameter and allow addition of parameter based on search criteria
		List<Object> preparedStmtList = new ArrayList<>();

		/*passed the preparedStmtList and search criteria inside the getWaterTankerQuery method
		 developed inside query builder to build and get the data as per search criteria*/
		String query = queryBuilder.getWaterTankerQuery(waterTankerBookingSearchCriteria, preparedStmtList);
		log.info("Final query for getWaterTankerBookingDetails {} and paramsList {} : " , preparedStmtList);
		/*
		*  Execute the query using JdbcTemplate with a generic row mapper
		*  Converts result set directly to a list of WaterTankerBookingDetail objects
		*  Uses custom GenericRowMapper for flexible and recursive object mapping
		* */
		return jdbcTemplate.query(query, preparedStmtList.toArray(), new GenericRowMapper<>(WaterTankerBookingDetail.class));
	}

	@Override
	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getWaterTankerQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		log.info("Final query for getWaterTankerBookingDetails {} and paramsList {} : " , preparedStatement);

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}
	
	@Override
	public void updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest) {
		log.info("Updating water tanker request data for booking no : "
				+ waterTankerRequest.getWaterTankerBookingDetail().getBookingNo());
		producer.push(requestServiceConfiguration.getWaterTankerApplicationUpdateTopic(), waterTankerRequest);

	}




}
