package org.egov.pg.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pg.config.AppProperties;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.repository.ServiceCallRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import com.jayway.jsonpath.JsonPath;

import lombok.Getter;

@Getter
public class CommonUtils {
	
	@Autowired
	AppProperties configs;
	
	@Autowired
	ServiceCallRepository restRepo;
	
	/*********************** MDMS Utitlity Methods *****************************/
	
    /**
     *Fetches all the values of particular attribute as map of fieldname to list
     *
     * @param tenantId tenantId of properties in PropertyRequest
     * @param names List of String containing the names of all masterdata whose code has to be extracted
     * @param requestInfo RequestInfo of the received PropertyRequest
     * @return Map of MasterData name to the list of code in the MasterData
     *
     */
    public Map<String,List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names, String filter,String jsonpath, RequestInfo requestInfo){

    	StringBuilder uri = new StringBuilder(configs.getMdmsServiceahost()).append(configs.getMdmsServiceSearchEndpoint());
        MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId,moduleName,names,filter,requestInfo);
        Optional<Object> response = restRepo.fetchResult(uri, criteriaReq);
        
        try {
        	if(response.isPresent()) {
                return JsonPath.read(response.get(),jsonpath);
        	}
		} catch (Exception e) {
			throw new CustomException(PgConstants.INVALID_TENANT_ID_MDMS_KEY,
					PgConstants.INVALID_TENANT_ID_MDMS_MSG);
		}
        
        return null;
    }
	
    public MdmsCriteriaReq prepareMdMsRequest(String tenantId,String moduleName, List<String> names, String filter, RequestInfo requestInfo) {

        List<MasterDetail> masterDetails = new ArrayList<>();

        names.forEach(name -> {
            masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
        });

        ModuleDetail moduleDetail = ModuleDetail.builder()
                .moduleName(moduleName).masterDetails(masterDetails).build();
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(moduleDetail);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }
}
