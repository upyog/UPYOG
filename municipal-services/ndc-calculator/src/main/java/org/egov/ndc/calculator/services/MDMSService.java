package org.egov.ndc.calculator.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.ndc.calculator.config.NDCCalculatorConfig;
import org.egov.ndc.calculator.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ndc.calculator.utils.NDCConstants;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class MDMSService {

	@Autowired
	 private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private NDCCalculatorConfig config;
	
//    public Object mDMSCall(CalculationReq calculationReq, String tenantId){
//        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(calculationReq,tenantId);
//        StringBuilder url = getMdmsSearchUrl();
//        Object result = serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
//        return result;
//    }
//
//    /**
//     * Creates MDMS request
//     * @param requestInfo The RequestInfo of the calculationRequest
//     * @param tenantId The tenantId of the tradeLicense
//     * @return MDMSCriteria Request
//     */
//    private MdmsCriteriaReq getMDMSRequest(CalculationReq calculationReq, String tenantId) {
//    	RequestInfo requestInfo = 	calculationReq.getRequestInfo();
//        List<MasterDetail> bpaMasterDetails = new ArrayList<>();
//
//        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_CALCULATIONTYPE)
//        		.build());
//        ModuleDetail bpaModuleDtls = ModuleDetail.builder().masterDetails(bpaMasterDetails)
//                .moduleName(BPACalculatorConstants.MDMS_BPA).build();
//
//        List<ModuleDetail> moduleDetails = new ArrayList<>();
//
//        moduleDetails.add(bpaModuleDtls);
//
//        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
//                .build();
//
//        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
//    }
//
//
//    /**
//     * Gets the calculationType for the city for a particular financialYear
//     * If for particular financialYear entry is not there previous year is taken
//     * If MDMS data is not available default values are returned
//     * @param requestInfo The RequestInfo of the calculationRequest
//     * @param license The tradeLicense for which calculation is done
//     * @return Map contianing the calculationType for TradeUnit and accessory
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	public Map getCalculationType(RequestInfo requestInfo,BPA bpa,Object mdmsData, String feeType){
//        HashMap<String,Object> calculationType = new HashMap<>();
//        try {
//            List jsonOutput = JsonPath.read(mdmsData, BPACalculatorConstants.MDMS_CALCULATIONTYPE_PATH);
//            LinkedHashMap responseMap = edcrService.getEDCRDetails(requestInfo, bpa);
//
//            String jsonString = new JSONObject(responseMap).toString();
//    		DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonString);
//    		Map<String, String> additionalDetails = new HashMap<String, String>();
//    		JSONArray serviceType = context.read("edcrDetail.*.applicationSubType");
//    		if (CollectionUtils.isEmpty(serviceType)) {
//    			serviceType.add("NEW_CONSTRUCTION");
//    		}
//    		JSONArray applicationType = context.read("edcrDetail.*.appliactionType");
//    		if (StringUtils.isEmpty(applicationType)) {
//    			applicationType.add("permit");
//    		}
//    		additionalDetails.put("serviceType", serviceType.get(0).toString());
//    		additionalDetails.put("applicationType", applicationType.get(0).toString());
//
//
//            log.debug("applicationType is " + additionalDetails.get("applicationType"));
//            log.debug("serviceType is " + additionalDetails.get("serviceType"));
//            String filterExp = "$.[?((@.applicationType == '"+ additionalDetails.get("applicationType")+"' || @.applicationType === 'ALL' ) &&  @.feeType == '"+feeType+"')]";
//            List<Object> calTypes = JsonPath.read(jsonOutput, filterExp);
//
//            filterExp = "$.[?(@.serviceType == '"+ additionalDetails.get("serviceType")+"' || @.serviceType === 'ALL' )]";
//            calTypes = JsonPath.read(calTypes, filterExp);
//
//            filterExp = "$.[?(@.riskType == '"+bpa.getRiskType()+"' || @.riskType === 'ALL' )]";
//            calTypes = JsonPath.read(calTypes, filterExp);
//
//            if(calTypes.size() > 1){
//	            	filterExp = "$.[?(@.riskType == '"+bpa.getRiskType()+"' )]";
//	            	calTypes  = JsonPath.read(calTypes, filterExp);
//            }
//
//            if(calTypes.size() == 0) {
//            		return defaultMap(feeType);
//            }
//
//             Object obj = calTypes.get(0);
//
//            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
//
//           String financialYear = currentYear + "-" + (currentYear + 1);
//           System.out.println(financialYear);
//
//            calculationType = (HashMap<String, Object>) obj;
//        }
//        catch (Exception e){
//            throw new CustomException(BPACalculatorConstants.CALCULATION_ERROR, "Failed to get calculationType");
//        }
//
//        return calculationType;
//    }
   
    /**
     * Creates and return default calculationType values as map
     * @return default calculationType Map
     */	
//    private Map defaultMap(String feeType){
//        Map defaultMap = new HashMap();
//        String feeAmount = ( feeType.equalsIgnoreCase(BPACalculatorConstants.MDMS_CALCULATIONTYPE_APL_FEETYPE) ) ? config.getApplFeeDefaultAmount() : config.getSancFeeDefaultAmount();
//        defaultMap.put( BPACalculatorConstants.MDMS_CALCULATIONTYPE_AMOUNT,feeAmount);
//        return defaultMap;
//    }

    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {

        // master details for TL module
        List<MasterDetail> flatFeeMasterDetails = new ArrayList<>();
        // filter to only get code field from master data

        final String flatFeeFilter = "$.[?(@.flatFee!=null)]";

        flatFeeMasterDetails.add(MasterDetail.builder().name(NDCConstants.NDC_FEE_MODULE).filter(flatFeeFilter).build());
        ModuleDetail flatFeeModule = ModuleDetail.builder().masterDetails(flatFeeMasterDetails)
                .moduleName(NDCConstants.NDC_MODULE.toLowerCase()).build();

        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(flatFeeModule);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }

    @Cacheable(value = "mdmsCache", key = "'tenantId'", sync = true)
    public Object mDMSCall(RequestInfo requestInfo,String tenantId){
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo,tenantId);
        StringBuilder url = getMdmsSearchUrl();
        Object result = serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
        return result;
    }

    private StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
    }


}
