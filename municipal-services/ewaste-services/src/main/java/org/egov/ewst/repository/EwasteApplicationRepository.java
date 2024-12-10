package org.egov.ewst.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.repository.builder.EwasteApplicationQueryBuilder;
import org.egov.ewst.repository.rowmapper.EwasteApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class EwasteApplicationRepository {

	@Autowired
	private EwasteApplicationQueryBuilder queryBuilder1;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EwasteApplicationRowMapper eRowMapper;

	public List<EwasteApplication> getApplication(EwasteApplicationSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder1.getEwasteApplicationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), eRowMapper);
	}
}