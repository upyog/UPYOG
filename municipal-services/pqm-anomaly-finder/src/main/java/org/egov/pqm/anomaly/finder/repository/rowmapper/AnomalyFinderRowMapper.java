package org.egov.pqm.anomaly.finder.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.egov.pqm.anomaly.finder.web.model.AnomalyType;
import org.egov.pqm.anomaly.finder.web.model.AuditDetails;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomaly;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnomalyFinderRowMapper implements ResultSetExtractor<List<PqmAnomaly>> {

  @Autowired
  private ObjectMapper mapper;

  @SuppressWarnings("rawtypes")
  @Override
  public List<PqmAnomaly> extractData(ResultSet rs) throws SQLException {

    Map<String, PqmAnomaly> pqmAnomalyMap = new LinkedHashMap<>();
    while (rs.next()) {
      String id = rs.getString("id");
      PqmAnomaly currentpqmAnomaly = pqmAnomalyMap.get(id);

      if (currentpqmAnomaly == null) {
        String anomalyTypeString = rs.getString("anomalyType");
        AnomalyType anomalyType = AnomalyType.valueOf(anomalyTypeString.toUpperCase());

        AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
            .createdTime(rs.getLong("createdTime"))
            .lastModifiedBy(rs.getString("lastModifiedBy"))
            .lastModifiedTime(rs.getLong("lastModifiedTime"))
            .build();

        currentpqmAnomaly = PqmAnomaly.builder().id(id)
            .tenantId(rs.getString("tenantId"))
            .testId(rs.getString("testId"))
            .plantCode(rs.getString("plantCode"))
            .anomalyType(anomalyType)
            .description(rs.getString("description"))
            .referenceId(rs.getString("description"))
            .resolutionStatus(rs.getString("description"))
            .additionalDetails(getAdditionalDetail("additionaldetails", rs))
            .isActive(rs.getBoolean("isActive"))
            .auditDetails(auditdetails)
            .build();

        pqmAnomalyMap.put(id, currentpqmAnomaly);
      }
    }

    // Set the list of documents to the test object outside the loop
    return new ArrayList<>(pqmAnomalyMap.values());

  }

  private JsonNode getAdditionalDetail(String columnName, ResultSet rs) {

    JsonNode additionalDetail = null;
    try {
      PGobject pgObj = (PGobject) rs.getObject(columnName);
      if (pgObj != null) {
        additionalDetail = mapper.readTree(pgObj.getValue());
      }
    } catch (IOException | SQLException e) {
      throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
    }
    return additionalDetail;
  }
}