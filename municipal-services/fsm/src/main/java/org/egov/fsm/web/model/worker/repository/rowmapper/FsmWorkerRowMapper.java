package org.egov.fsm.web.model.worker.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.egov.fsm.web.model.AuditDetails;
import org.egov.fsm.web.model.worker.Worker;
import org.egov.fsm.web.model.worker.WorkerStatus;
import org.egov.fsm.web.model.worker.WorkerType;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class FsmWorkerRowMapper implements ResultSetExtractor<List<Worker>> {

  @Autowired
  private ObjectMapper mapper;

  @Getter
  private int fullCount = 0;


  public void setFullCount(int fullCount) {
    this.fullCount = fullCount;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Worker> extractData(ResultSet rs) throws SQLException {

    Map<String, Worker> workerMap = new LinkedHashMap<>();
    this.setFullCount(0);
    while (rs.next()) {
      String id = rs.getString("id");
      String applicationId = rs.getString("application_id");
      Worker currentWorker = workerMap.get(id);
      String tenantId = rs.getString("tenantid");
      String individualId = rs.getString("individual_id");
      String workerType = rs.getString("workerType");
      String status = rs.getString("status");
      this.setFullCount(rs.getInt("full_count"));
      if (workerMap.get(id) == null) {
        currentWorker = Worker.builder().id(id).applicationId(applicationId).tenantId(tenantId)
            .individualId(individualId)
            .additionalDetails(getAdditionalDetail("additionalDetails", rs))
            .status(WorkerStatus.fromValue(status))
            .workerType(WorkerType.fromValue(workerType)).build();

        workerMap.put(id, currentWorker);
      }
      addAuditDetails(rs, currentWorker);
    }

    return new ArrayList<>(workerMap.values());
  }

  private void addAuditDetails(ResultSet rs, Worker worker) throws SQLException {
    worker.setAuditDetails(AuditDetails.builder()
        .createdBy(rs.getString("createdBy"))
        .createdTime(rs.getLong("createdTime"))
        .lastModifiedBy(rs.getString("lastModifiedBy"))
        .lastModifiedTime(rs.getLong("lastModifiedTime"))
        .build());
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