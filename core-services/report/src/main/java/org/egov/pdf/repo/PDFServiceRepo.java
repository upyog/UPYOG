package org.egov.pdf.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.egov.tracer.model.CustomException;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PDFServiceRepo {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@PostConstruct
	private void init() {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
	}

	public Map<String, Object> excutePdfServiceQuery(String queryKey, String query, Map<String, Object> parameters,
			Map<String, String> responseMapping) {
		MapSqlParameterSource params = new MapSqlParameterSource(parameters);
		List<Map<String, Object>> maps = null;

		try {
			maps = namedParameterJdbcTemplate.queryForList(query, params);
			return parseToContextMap(queryKey, maps, responseMapping);
		} catch (DataAccessResourceFailureException ex) {
			log.info("Query Execution Failed Due To Timeout: ", ex);
			PSQLException cause = (PSQLException) ex.getCause();
			if (cause != null && cause.getSQLState().equals("57014")) {
				throw new CustomException("QUERY_EXECUTION_TIMEOUT", "Query failed, as it took more than: "
						+ (jdbcTemplate.getQueryTimeout()) + " seconds to execute");
			} else {
				throw ex;
			}
		} catch (Exception e) {
			log.info("Query Execution Failed: ", e);
			throw new CustomException("QUERY_EXEC_ERROR", "Error while executing query: " + e.getMessage());
		}
	}

	private Map<String, Object> parseToContextMap(String queryKey, List<Map<String, Object>> maps,
			Map<String, String> responseMapping) {
		Map<String, Object> contextSqlMap = new HashMap<>();
		if (maps != null && !maps.isEmpty()) {
			if (maps.size() > 1) {
				List<Map<String, Object>> contextRows = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> row : maps) {
					Map<String, Object> contextRow = new HashMap<>();
					parseRow(row, contextRow, responseMapping);
					contextRows.add(contextRow);
				}
				contextSqlMap.put(queryKey, contextRows);
			} else {
				Map<String, Object> map = maps.get(0);
				parseRow(map, contextSqlMap, responseMapping);
			}
		}
		return contextSqlMap;
	}

	private void parseRow(Map<String, Object> qryRow, Map<String, Object> contextRow,
			Map<String, String> responseMapping) {
		for (Map.Entry<String, String> e : responseMapping.entrySet()) {
			String variable = e.getKey();
			String columnName = e.getValue();
			contextRow.put(variable, qryRow.get(columnName));
		}
	}
}
