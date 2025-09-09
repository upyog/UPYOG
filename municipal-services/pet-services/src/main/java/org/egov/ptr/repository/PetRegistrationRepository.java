package org.egov.ptr.repository;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.repository.builder.PetApplicationQueryBuilder;
import org.egov.ptr.repository.rowmapper.PetApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;




/**
 * The PetRegistrationRepository class is responsible for database operations
 * related to pet registration applications. It provides methods to:
 * 
 * - Retrieve pet registration applications based on search criteria.
 * - Fetch tenant IDs for pet applications.
 * 
 * This class uses:
 * - PetApplicationQueryBuilder to dynamically construct SQL queries.
 * - JdbcTemplate for executing SQL queries and interacting with the database.
 * - PetApplicationRowMapper to map database rows to Java objects.
 */
@Slf4j
@Repository
public class PetRegistrationRepository {

	@Autowired
	private PetApplicationQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PetApplicationRowMapper rowMapper;

	public List<PetRegistrationApplication> getApplications(PetApplicationSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPetApplicationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}

	public List<String> fetchPetApplicationTenantIds() {
		List<Object> preparedStmtList = new ArrayList<>();
		return jdbcTemplate.query(queryBuilder.TENANTIDQUERY, preparedStmtList.toArray(),
				new SingleColumnRowMapper<>(String.class));

	}
}