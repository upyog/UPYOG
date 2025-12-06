package org.egov.pqm.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.repository.ServiceRequestRepository;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.mdms.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MDMSUtils {

  @Autowired
  private ServiceRequestRepository serviceRequestRepository;

  @Autowired
  private ServiceConfiguration config;

  @Autowired
  private ObjectMapper objectMapper;

  public static final String filterCode = "$.*.code";

  public Object fetchMdmsData(RequestInfo requestInfo, String tenantId, String moduleName,
      List<String> masterNameList) {
    org.egov.mdms.model.MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId, moduleName, masterNameList);
    Object response = new HashMap<>();
    MdmsResponse mdmsResponse = new MdmsResponse();
    try {
       response = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
    } catch (Exception e) {
      log.error("Exception occurred while fetching category lists from mdms: ", e);
    }

    return response;

  }

  private org.egov.mdms.model.MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId,
      String moduleName, List<String> masterNameList) {
    List<MasterDetail> masterDetailList = new ArrayList<>();
    for (String masterName : masterNameList) {
      MasterDetail masterDetail = new MasterDetail();
      masterDetail.setName(masterName);
      masterDetailList.add(masterDetail);
    }

    ModuleDetail moduleDetail = new ModuleDetail();
    moduleDetail.setMasterDetails(masterDetailList);
    moduleDetail.setModuleName(moduleName);
    List<ModuleDetail> moduleDetailList = new ArrayList<>();
    moduleDetailList.add(moduleDetail);

    org.egov.mdms.model.MdmsCriteria mdmsCriteria = new org.egov.mdms.model.MdmsCriteria();
    mdmsCriteria.setTenantId(tenantId.split("\\.")[0]);
    mdmsCriteria.setModuleDetails(moduleDetailList);

    org.egov.mdms.model.MdmsCriteriaReq mdmsCriteriaReq = new org.egov.mdms.model.MdmsCriteriaReq();
    mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
    mdmsCriteriaReq.setRequestInfo(requestInfo);

    return mdmsCriteriaReq;
  }

  /**
   * Returns the url for mdms search v1 endpoint
   *
   * @return url for mdms search v1 endpoint
   */
  public StringBuilder getMdmsSearchUrl() {
    return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
  }

  /**
   * Returns the url for mdms search v2 endpoint
   *
   * @return url for mdms search v2 endpoint
   */
  public StringBuilder getMdmsSearchUrl2() {
    return new StringBuilder().append(config.getMdmsHostv2()).append(config.getMdmsv2EndPoint());
  }

  private ModuleDetail getTenantModuleRequestData() {
    List<MasterDetail> tenantMasterDetails = new ArrayList<>();

    ModuleDetail tenantModuleDetail = null;
    return tenantModuleDetail;
  }

  public Object mdmsCallV2(RequestInfo requestInfo, String tenantId, String schemaCode, List<String> uniqueIdentifiers){
    MdmsCriteriaRequest mdmsCriteriaRequest = getMDMSRequestV2(requestInfo, tenantId, schemaCode, uniqueIdentifiers);
    StringBuilder uri = getMdmsSearchUrl2();
    Object result = serviceRequestRepository.fetchResult(uri, mdmsCriteriaRequest);
    return result;
  }

  public MdmsCriteriaRequest getMDMSRequestV2(RequestInfo requestInfo , String  tenantId ,
      String schemaCode, List<String> uniqueIdentifiers){
    org.egov.pqm.web.model.mdms.MdmsCriteria mdmsCriteria = org.egov.pqm.web.model.mdms.MdmsCriteria.builder().tenantId(tenantId).schemaCode(schemaCode).limit(config.getMdmsv2MaxLimit()).isActive(true).build();

    if(!uniqueIdentifiers.isEmpty())
      mdmsCriteria.setUniqueIdentifiers(uniqueIdentifiers);

    MdmsCriteriaRequest mdmsCriteriaRequest =
        MdmsCriteriaRequest.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
    return mdmsCriteriaRequest;
  }

  /**
   * Parsing Json Data to a Code-QualityCriteria Map
   *
   * @param jsonData Json Data
   * @return Map of Code-QualityCriteria
   */
  public static Map<String, MDMSQualityCriteria> parseJsonToMap(String jsonData) {
    Map<String, MDMSQualityCriteria> codeToQualityCriteriaMap = new HashMap<>();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(jsonData);
      JsonNode qualityCriteriaArray = jsonNode.get("mdms");

      for (JsonNode criteriaNode : qualityCriteriaArray) {
        String code = criteriaNode.get("data").get("code").asText();
        MDMSQualityCriteria qualityCriteria = objectMapper.convertValue(criteriaNode.get("data"),
            MDMSQualityCriteria.class);

        codeToQualityCriteriaMap.put(code, qualityCriteria);
      }
    } catch (Exception e) {
      throw new CustomException(ErrorConstants.PARSING_ERROR,
          "Unable to make Code-QualityCriteria Map");
    }

    return codeToQualityCriteriaMap;
  }

  /**
   * Parsing Json Data to a Code-QualityCriteria Map
   *
   * @param jsonData Json Data
   * @return Map of Code-QualityCriteria
   */
  public static List<MdmsTest> parseJsonToTestList(String jsonData) {
    List<MdmsTest> testList = new ArrayList<>();
    List<JsonNode> errorMap = new ArrayList<>();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(jsonData);
      JsonNode testArray = jsonNode.get("mdms");

      for (JsonNode criteriaNode : testArray) {
        MdmsTest test = null;
        try {
          test = objectMapper.convertValue(criteriaNode.get("data"),
                  MdmsTest.class);
        } catch (Exception e) {
          errorMap.add(criteriaNode);
        }

        if (test != null)
          testList.add(test);
      }
    } catch (Exception e) {
      log.error(ErrorConstants.PARSING_ERROR, errorMap);
    }

    return testList;
  }

  public List<String> extractTenantCode(String jsonString) {
    List<String> codes = new ArrayList<>();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(jsonString);

      JsonNode tenantsNode = rootNode.path("MdmsRes").path("tenant").path("tenants");

      for (JsonNode tenantNode : tenantsNode) {
        JsonNode codeNode = tenantNode.path("code");
        String code = codeNode.asText();

        if (code.contains(".")) {
          codes.add(code);
        }
      }
    } catch (Exception e) {
      log.error(ErrorConstants.PARSING_ERROR, "Cannot parse tenants master");
    }

    return codes;
  }

    public String fetchParsedMDMSData(RequestInfo requestInfo, String tenantId, String schemaCode)
    {
        //fetch mdms data for QualityCriteria Master
        Object jsondata = mdmsCallV2(requestInfo,
                tenantId.split("\\.")[0], schemaCode, new ArrayList<>());
        String jsonString = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(jsondata);
        } catch (Exception e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR,
                    "Unable to parse QualityCriteria mdms data ");
        }
        return jsonString;
    }

}