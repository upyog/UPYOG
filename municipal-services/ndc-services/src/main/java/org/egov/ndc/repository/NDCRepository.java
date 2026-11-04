package org.egov.ndc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.egov.ndc.repository.builder.NdcQueryBuilder;
import org.egov.ndc.repository.rowmapper.NdcRowMapper;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class NDCRepository {

	private final NdcQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final NdcRowMapper rowMapper;

	public NDCRepository(NdcQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate, NdcRowMapper rowMapper) {
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	public Set<String> getExistingUuids(String tableName, List<String> uuids) {
		String sql = queryBuilder.getExistingUuids(tableName, uuids);
		return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("uuid")).stream().collect(Collectors.toSet());
	}

	public boolean checkApplicationExists(String uuid) {
		String sql = queryBuilder.checkApplicationExists();
		String query = jdbcTemplate.queryForObject(sql, String.class, uuid);
		return query != null;
	}

	public List<Application> fetchNdcApplications(NdcApplicationSearchCriteria criteria) {
		List<Object> uuidStmtList = new ArrayList<>();
		String uuidQuery = queryBuilder.getPaginatedApplicationUuids(criteria, uuidStmtList);
		log.info("UUID Query: {}", uuidQuery);
		log.info("UUID Params: {}", uuidStmtList);

		List<String> paginatedUuids = jdbcTemplate.query(uuidQuery, (rs, rowNum) -> rs.getString("uuid"),
				uuidStmtList.toArray());

		if (paginatedUuids.isEmpty()) {
			return new ArrayList<>();
		}

		List<Object> detailStmtList = new ArrayList<>();
		String detailQuery = queryBuilder.getNdcApplicationDetailsQuery(paginatedUuids, detailStmtList);
		log.info("Detail Query: {}", detailQuery);
		log.info("Detail Params: {}", detailStmtList);

		return jdbcTemplate.query(detailQuery, rowMapper, detailStmtList.toArray());
	}

}
