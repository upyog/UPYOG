package org.egov.pqm.anomaly.finder.repository;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.pqm.anomaly.finder.config.PqmAnomalyConfiguration;
import org.egov.pqm.anomaly.finder.producer.PqmAnomalyFinderProducer;
import org.egov.pqm.anomaly.finder.repository.querybuilder.AnomalyFinderQueryBuilder;
import org.egov.pqm.anomaly.finder.repository.rowmapper.AnomalyFinderRowMapper;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomaly;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomalyRequest;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomalySearchCriteria;
import org.egov.pqm.anomaly.finder.web.model.TestRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AnomalyRepository {

	@Autowired
	private PqmAnomalyFinderProducer pqmAnomalyFinderProducer;

	@Autowired
	private PqmAnomalyConfiguration pqmAnomalyConfiguration;

	@Autowired
	private AnomalyFinderQueryBuilder anomalyFinderQueryBuilder;

	@Autowired
	private AnomalyFinderRowMapper anomalyFinderRowMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void save(PqmAnomalyRequest pqmAnomalyRequest) {
		pqmAnomalyFinderProducer.push(pqmAnomalyConfiguration.getSaveTestAnomalyTopic(), pqmAnomalyRequest);
	}

	/*
	 * Added for testing purpose will remove once we done with testing
	 */
	public void save(TestRequest testRequest) {
		pqmAnomalyFinderProducer.push("create-pqm-anomaly-finder", testRequest);
	}

	public List<PqmAnomaly> getAnomalyFinderData(List<String> testIdLists) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = anomalyFinderQueryBuilder.anomalySearchQuery(testIdLists, preparedStmtList);
		List<PqmAnomaly> pqmAnomalys = jdbcTemplate.query(query, preparedStmtList.toArray(), anomalyFinderRowMapper);
		return pqmAnomalys;
	}

	public List<PqmAnomaly> getAnomalyDataForCriteria(PqmAnomalySearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = anomalyFinderQueryBuilder.anomalySearchQueryWithCriteria(searchCriteria, preparedStmtList);
		log.info("query->                           "+query);
		List<PqmAnomaly> pqmAnomalys = jdbcTemplate.query(query, preparedStmtList.toArray(), anomalyFinderRowMapper);
		log.info("preparedStmtList -> " +preparedStmtList);
		return pqmAnomalys;
	}
	public List<String> fetchPqmAnomalyIds(@Valid PqmAnomalySearchCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(criteria.getOffset());
		preparedStmtList.add(criteria.getLimit());
		return jdbcTemplate.query(
				"SELECT id from eg_pqm_anomaly_details ORDER BY createdtime offset " + " ? " + "limit ? ",
				preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
	}

	public List<PqmAnomaly> getPqmAnomalyPlainSearch(PqmAnomalySearchCriteria criteria) {

		if (criteria.getIds() == null || criteria.getIds().isEmpty())
			throw new CustomException("PLAIN_SEARCH_ERROR", "Search only allowed by ids!");

		List<Object> preparedStmtList = new ArrayList<>();
		String query = anomalyFinderQueryBuilder.getPqmAnomalyLikeQuery(criteria, preparedStmtList);
		log.info("Query: " + query);
		log.info("PS: " + preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), anomalyFinderRowMapper);
	}

}
