package org.egov.fsm.plantmapping.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.fsm.plantmapping.config.PlantMappingConfiguration;
import org.egov.fsm.plantmapping.web.model.mdms.MdmsCriteriaRequest;
import org.egov.fsm.repository.ServiceRequestRepository;
import org.egov.fsm.util.FSMErrorConstants;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class MDMSUtils {

  @Autowired
  private ServiceRequestRepository serviceRequestRepository;

  @Autowired
  private PlantMappingConfiguration config;

  @Autowired
  private ObjectMapper objectMapper;

  public static final String filterCode = "$.*.code";

  /**
   * Returns the url for mdms search v2 endpoint
   *
   * @return url for mdms search v2 endpoint
   */
  public StringBuilder getMdmsSearchUrl2() {
    return new StringBuilder().append(config.getMdmsHostv2()).append(config.getMdmsv2EndPoint());
  }

  public Object mdmsCallV2(RequestInfo requestInfo, String tenantId, String schemaCode, List<String> uniqueIdentifiers){
    MdmsCriteriaRequest mdmsCriteriaRequest = getMDMSRequestV2(requestInfo, tenantId, schemaCode, uniqueIdentifiers);
    StringBuilder uri = getMdmsSearchUrl2();
    Object result = serviceRequestRepository.fetchResult(uri, mdmsCriteriaRequest);
    return result;
  }

  public MdmsCriteriaRequest getMDMSRequestV2(RequestInfo requestInfo , String  tenantId ,
      String schemaCode, List<String> uniqueIdentifiers){
    org.egov.fsm.plantmapping.web.model.mdms.MdmsCriteria mdmsCriteria = org.egov.fsm.plantmapping.web.model.mdms.MdmsCriteria.builder().tenantId(tenantId).schemaCode(schemaCode).limit(config.getMdmsv2MaxLimit()).isActive(true).build();

    if(!uniqueIdentifiers.isEmpty())
      mdmsCriteria.setUniqueIdentifiers(uniqueIdentifiers);

    MdmsCriteriaRequest mdmsCriteriaRequest =
        MdmsCriteriaRequest.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
    return mdmsCriteriaRequest;
  }
  

}