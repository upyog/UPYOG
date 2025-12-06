package org.egov.pqm.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pqm.web.model.AuditDetails;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.TestResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QualityCriteriaRowMapper implements ResultSetExtractor<List<QualityCriteria>> {
	
	@Autowired
	private ObjectMapper mapper;
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<QualityCriteria> extractData(ResultSet rs) throws SQLException {

		Map<String, QualityCriteria> qualityCriteriaMap = new LinkedHashMap<>();
		while (rs.next()) {
			String id = rs.getString("id");
			QualityCriteria currentQualityCriteria = qualityCriteriaMap.get(id);
			
			if (currentQualityCriteria == null) {
				String resultStatusString = rs.getString("resultStatus");
				TestResultStatus resultStatus = TestResultStatus.valueOf(resultStatusString.toUpperCase());
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
						.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
						.lastModifiedTime(rs.getLong("lastModifiedTime")).build();

				currentQualityCriteria = QualityCriteria.builder().id(id).testId(rs.getString("testId"))
						.criteriaCode(rs.getString("criteriaCode")).resultValue(rs.getBigDecimal("resultValue"))
						.resultStatus(resultStatus)
						.isActive( rs.getBoolean("isActive")).auditDetails(auditdetails)
						.build();

				qualityCriteriaMap.put(id, currentQualityCriteria);
			}
		}

		// Set the list of documents to the test object outside the loop
		return new ArrayList<>(qualityCriteriaMap.values());

	}

}
