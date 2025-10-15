package org.egov.ndc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.producer.Producer;
import org.egov.ndc.repository.builder.NdcQueryBuilder;
import org.egov.ndc.repository.rowmapper.NdcRowMapper;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class NDCRepository {
	
	@Autowired
	private Producer producer;
	
	@Autowired
	private NDCConfiguration config;	

	@Autowired
	private NdcQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NdcRowMapper rowMapper;


	public Set<String> getExistingUuids(String tableName, List<String> uuids) {
		String sql = queryBuilder.getExistingUuids(tableName, uuids);
		return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("uuid")).stream().collect(Collectors.toSet());
	}

	public boolean checkApplicationExists(String uuid) {
		String sql = queryBuilder.checkApplicationExists(uuid);
		String query = jdbcTemplate.queryForObject(sql, new Object[]{uuid}, String.class);
		return query != null;
	}

	public List<Application> fetchNdcApplications(NdcApplicationSearchCriteria criteria) {
		List<Object> uuidStmtList = new ArrayList<>();
		String uuidQuery = queryBuilder.getPaginatedApplicationUuids(criteria, uuidStmtList);
		log.info("UUID Query: {}", uuidQuery);
		log.info("UUID Params: {}", uuidStmtList);

		List<String> paginatedUuids = jdbcTemplate.query(uuidQuery, uuidStmtList.toArray(), (rs, rowNum) -> rs.getString("uuid"));

		if (paginatedUuids.isEmpty()) {
			return new ArrayList<>();
		}

		List<Object> detailStmtList = new ArrayList<>();
		String detailQuery = queryBuilder.getNdcApplicationDetailsQuery(paginatedUuids, detailStmtList);
		log.info("Detail Query: {}", detailQuery);
		log.info("Detail Params: {}", detailStmtList);

		return jdbcTemplate.query(detailQuery, detailStmtList.toArray(), rowMapper);
	}

}