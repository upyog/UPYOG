package org.egov.pqm.validator;

import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_CRITERIA;
import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_MATERIAL;
import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_PLANT;
import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_PROCESS;
import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_STAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.util.MDMSUtils;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.TestRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component

public class MDMSValidator {

  private Map<String, Object> mdmsResMap;
  @Autowired
  private MDMSUtils mdmsUtils;

  public void validateIfMasterPresent(RequestInfo requestInfo, String tenantId,
      String schemaCode, String uniqueId) {
    Object mdmsData = mdmsUtils.mdmsCallV2(requestInfo, tenantId, schemaCode, new ArrayList<>() );
    List<Object> result = JsonPath.read(mdmsData,
        "$.mdms[?(@.uniqueIdentifier == '" + uniqueId + "')]");
    if (result.isEmpty()) {
      throw new CustomException(
          "INVALID" + " " + schemaCode.substring(schemaCode.lastIndexOf('.') + 1).toUpperCase(),
          schemaCode.substring(schemaCode.lastIndexOf('.') + 1).toUpperCase()
              + " " + "code is not present in mdms");
    }
  }

  public void validateMdmsData(TestRequest testRequest) {
    String tenantId = testRequest.getTests().get(0).getTenantId().split("\\.")[0];
    validateIfMasterPresent(testRequest.getRequestInfo(), tenantId, PQM_SCHEMA_CODE_PLANT,
        testRequest.getTests().get(0).getPlantCode());
    validateIfMasterPresent(testRequest.getRequestInfo(), tenantId, PQM_SCHEMA_CODE_PROCESS,
        testRequest.getTests().get(0).getProcessCode());
    validateIfMasterPresent(testRequest.getRequestInfo(), tenantId, PQM_SCHEMA_CODE_STAGE,
        testRequest.getTests().get(0).getStageCode());
    validateIfMasterPresent(testRequest.getRequestInfo(), tenantId, PQM_SCHEMA_CODE_MATERIAL,
        testRequest.getTests().get(0).getMaterialCode());
    List<QualityCriteria> criteriaList = testRequest.getTests().get(0).getQualityCriteria();
    for (QualityCriteria criteria : criteriaList) {
      validateIfMasterPresent(testRequest.getRequestInfo(), tenantId, PQM_SCHEMA_CODE_CRITERIA,
          criteria.getCriteriaCode());
    }
  }
}