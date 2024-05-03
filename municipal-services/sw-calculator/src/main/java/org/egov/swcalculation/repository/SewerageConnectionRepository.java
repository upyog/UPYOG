package org.egov.swcalculation.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SewerageConnectionRepository {

	private static final String SW_ADDTNL_DTL_QUERY = "select additionaldetails from eg_sw_connection ";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public String fetchConnectionAdditonalDetails(String connectionNumber, String tenantId) {
		log.info("fetchSWConnectionAdditonalDetails- connectionNumber : "+connectionNumber);
		List<Object> presparedStmtList = new ArrayList<>();
		StringBuilder query = new StringBuilder(SW_ADDTNL_DTL_QUERY);
		query.append(" where tenantid=?");
		query.append(" and connectionno =?");
		presparedStmtList.add(tenantId);
		presparedStmtList.add(connectionNumber);
		return jdbcTemplate.queryForObject(query.toString(), presparedStmtList.toArray(), String.class);
	}

}
