package org.egov.fsm.plantmapping.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.fsm.plantmapping.util.MDMSUtils;
import org.egov.fsm.plantmapping.util.PlantMappingConstants;
import org.egov.fsm.plantmapping.web.model.PlantMappingRequest;
import org.egov.fsm.util.FSMErrorConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component

public class PlantMDMSValidator {

  private Map<String, Object> mdmsResMap;
  @Autowired
  private MDMSUtils mdmsUtils;

  public List<Object> validateIfMasterPresent(RequestInfo requestInfo, String tenantId,
			String schemaCode, String uniqueId) {
		Object mdmsData = mdmsUtils.mdmsCallV2(requestInfo, tenantId, schemaCode, new ArrayList<>());
		List<Object> result = JsonPath.read(mdmsData, "$.mdms[?(@.uniqueIdentifier == '" + uniqueId + "')]");
		if (result.isEmpty()) {
			throw new CustomException(
					"INVALID" + " " + schemaCode.substring(schemaCode.lastIndexOf('.') + 1).toUpperCase(),
					schemaCode.substring(schemaCode.lastIndexOf('.') + 1).toUpperCase() + " "
							+ "plant code is not present in mdms");
		}

		return result;
	}

  public void validateMdmsData(PlantMappingRequest request) {
    String tenantId = request.getPlantMapping().getTenantId().split("\\.")[0];
    List<Object> result=validateIfMasterPresent(request.getRequestInfo(), tenantId, "PQM.Plant",
    		request.getPlantMapping().getPlantCode() );
    validateFSTPPlantInfo(result,  request.getPlantMapping().getTenantId());
   
  }
  
  public void validateFSTPPlantInfo(List<Object> result, String tenantId) {
	  
	  Map<String, String> errorMap = new HashMap<>();
	  for (Object obj : result) {
          if (obj instanceof Map) {
              Map<String, Object> plantInfo = (Map<String, Object>) obj;
              
              // Check if the data field is present
              if (plantInfo.containsKey("data") && plantInfo.get("data") instanceof Map) {
                  Map<String, Object> data = (Map<String, Object>) plantInfo.get("data");
                  
                  // Check if ULBS field is present and meets validation criteria
                  if (data.containsKey("ULBS") && ! data.get("ULBS").toString().contains(tenantId)) {
                	  
          				errorMap.put(FSMErrorConstants.INVALID_FSTP_CODE, "Invalid FSTP code for the given tenantId");
                  }
                  
              }
          }
      }
	  if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
  }
}