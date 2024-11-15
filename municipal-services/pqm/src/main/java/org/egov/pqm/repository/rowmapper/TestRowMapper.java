package org.egov.pqm.repository.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pqm.web.model.*;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestRowMapper implements ResultSetExtractor<List<Test>> {


  @Autowired
  private ObjectMapper mapper;

  private int fullCount = 0;

  public int getFullCount() {
    return fullCount;
  }

  public void setFullCount(int fullCount) {
    this.fullCount = fullCount;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Test> extractData(ResultSet rs) throws SQLException {

    Map<String, Test> testMap = new LinkedHashMap<>();
    this.setFullCount(0);
    while (rs.next()) {
      String testId = rs.getString("testId");
      Test currentTest = testMap.get(testId);

      if (currentTest == null) {
        String id = rs.getString("id");
        String tenantId = rs.getString("tenantId");
        String plantCode = rs.getString("plantCode");
        String processCode = rs.getString("processCode");
        String stageCode = rs.getString("stageCode");
        String materialCode = rs.getString("materialCode");
        String deviceCode = rs.getString("deviceCode");
        String statusString = rs.getString("status");
        TestResultStatus status = TestResultStatus.valueOf(statusString.toUpperCase());
        String wfStatus = rs.getString("wfStatus");
        String testCode = rs.getString("testCode");

        String sourceTypeString = rs.getString("sourceType");
        SourceType sourceType = SourceType.valueOf(sourceTypeString.toUpperCase());
        String labAssignedTo = rs.getString("labAssignedTo");
        Long scheduledDate = rs.getLong("scheduledDate");
        Boolean isActive = rs.getBoolean("isActive");
        this.setFullCount(rs.getInt("full_count"));
        Object additionaldetails = getAdditionalDetail("additionaldetails", rs);
        AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby"))
            .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

        currentTest = Test.builder().id(id).testId(testId).tenantId(tenantId).plantCode(plantCode)
            .processCode(processCode).testCode(testCode)
            .stageCode(stageCode).materialCode(materialCode).deviceCode(deviceCode)
            .status(status).wfStatus(wfStatus).sourceType(sourceType).labAssignedTo(labAssignedTo)
            .scheduledDate(scheduledDate).isActive(isActive).additionalDetails(additionaldetails)
            .auditDetails(auditdetails).build();

        testMap.put(testId, currentTest);
      }
    }

    return new ArrayList<>(testMap.values());
  }

  private List<QualityCriteria> parseQualityCriteriaJson(String json) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, new TypeReference<List<QualityCriteria>>() {
      });
    } catch (IOException e) {
      throw new RuntimeException("Error parsing QualityCriteria JSON", e);
    }
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
