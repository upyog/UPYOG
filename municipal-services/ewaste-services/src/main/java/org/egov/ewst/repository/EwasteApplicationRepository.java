package org.egov.ewst.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.repository.builder.EwasteApplicationQueryBuilder;
import org.egov.ewst.repository.rowmapper.EwasteApplicationRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * EwasteApplicationRepository is responsible for interacting with the database
 * to perform CRUD operations on Ewaste applications.
 * It uses JdbcTemplate for executing SQL queries and EwasteApplicationRowMapper
 * for mapping the result set to EwasteApplication objects.
 */
@Slf4j
@Repository
public class EwasteApplicationRepository {

	private final EwasteApplicationQueryBuilder queryBuilder1;
	private final JdbcTemplate jdbcTemplate;
	private final EwasteApplicationRowMapper eRowMapper;

	public EwasteApplicationRepository(EwasteApplicationQueryBuilder queryBuilder1, JdbcTemplate jdbcTemplate,
			EwasteApplicationRowMapper eRowMapper) {
		this.queryBuilder1 = queryBuilder1;
		this.jdbcTemplate = jdbcTemplate;
		this.eRowMapper = eRowMapper;
	}

	/**
	 * Retrieves a list of Ewaste applications based on the provided search criteria.
	 *
	 * @param searchCriteria The criteria used to filter the Ewaste applications.
	 * @return A list of Ewaste applications that match the search criteria.
	 */
	public List<EwasteApplication> getApplication(EwasteApplicationSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder1.getEwasteApplicationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, eRowMapper, preparedStmtList.toArray());
	}
}
