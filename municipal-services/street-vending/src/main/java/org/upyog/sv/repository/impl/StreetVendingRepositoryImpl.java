package org.upyog.sv.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.kafka.Producer;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.repository.querybuilder.StreetVendingQueryBuilder;
import org.upyog.sv.repository.rowmapper.StreetVendingApplicationRowMapper;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class StreetVendingRepositoryImpl implements StreetVendingRepository {

	@Autowired
	private Producer producer;
	@Autowired
	private StreetVendingConfiguration vendingConfiguration;

	@Autowired
	private StreetVendingQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private StreetVendingApplicationRowMapper rowMapper;

	@Override
	public void save(StreetVendingRequest streetVendingRequest) {
		log.info("Saving community hall booking request data for booking no : "
				+ streetVendingRequest.getStreetVendingDetail().getApplicationNo());
		producer.push(vendingConfiguration.getStreetVendingApplicationSaveTopic(), streetVendingRequest);
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingApplications(
			StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getStreetVendingSearchQuery(streetVendingSearchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);

	}

}
