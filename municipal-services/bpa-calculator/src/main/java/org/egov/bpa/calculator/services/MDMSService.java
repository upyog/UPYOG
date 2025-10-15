package org.egov.bpa.calculator.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.bpa.calculator.config.BPACalculatorConfig;
import org.egov.bpa.calculator.repository.ServiceRequestRepository;
import org.egov.bpa.calculator.utils.BPACalculatorConstants;
import org.egov.bpa.calculator.web.models.CalculationReq;
import org.egov.bpa.calculator.web.models.bpa.BPA;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

	public static  final String businessService_BPA = "BPA";
	
	@Autowired
	 private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private BPACalculatorConfig config;

	@Autowired
	private EDCRService edcrService;
	
    public Object mDMSCall(CalculationReq calculationReq,String tenantId){
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(calculationReq,tenantId);
        StringBuilder url = getMdmsSearchUrl();
        Object result = serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
        return result;
    }

    /**
     * Creates and returns the url for mdms search endpoint
     *
     * @return MDMS Search URL
     */
    private StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
    }

    /**
     * Creates MDMS request
     * @param requestInfo The RequestInfo of the calculationRequest
     * @param tenantId The tenantId of the tradeLicense
     * @return MDMSCriteria Request
     */
    private MdmsCriteriaReq getMDMSRequest(CalculationReq calculationReq, String tenantId) {
    	RequestInfo requestInfo = 	calculationReq.getRequestInfo();
        List<MasterDetail> bpaMasterDetails = new ArrayList<>();
        
        // Start Financial Year data required to get the current financial year
        List<MasterDetail> fyMasterDetails = new ArrayList<>();
        final String filterCodeForUom = "$.[?(@.active==true)]";
        fyMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_FINANCIALYEAR).filter(filterCodeForUom).build());
        ModuleDetail fyModuleDtls = ModuleDetail.builder().masterDetails(fyMasterDetails)
                .moduleName(BPACalculatorConstants.MDMS_EGF_MASTER).build();
        // End Financial Year data required to get the current financial year
        
        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_CALCULATIONTYPE)
        		.build());
        bpaMasterDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_APPLICATION_FEES)
        		.build());
        ModuleDetail bpaModuleDtls = ModuleDetail.builder().masterDetails(bpaMasterDetails)
                .moduleName(BPACalculatorConstants.MDMS_BPA).build();

        List<ModuleDetail> moduleDetails = new ArrayList<>();
        
        moduleDetails.add(bpaModuleDtls);
        moduleDetails.add(fyModuleDtls);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }


    /**
     * Gets the calculationType for the city for a particular financialYear
     * If for particular financialYear entry is not there previous year is taken
     * If MDMS data is not available default values are returned
     * @param requestInfo The RequestInfo of the calculationRequest
     * @param license The tradeLicense for which calculation is done
     * @return Map contianing the calculationType for TradeUnit and accessory
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getCalculationType(RequestInfo requestInfo,BPA bpa,Object mdmsData, String feeType){
        HashMap<String,Object> calculationType = new HashMap<>();
        try {
            List jsonOutput = JsonPath.read(mdmsData, BPACalculatorConstants.MDMS_CALCULATIONTYPE_PATH);
            LinkedHashMap responseMap = edcrService.getEDCRDetails(requestInfo, bpa);
            
            String jsonString = new JSONObject(responseMap).toString();
    		DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonString);
    		Map<String, String> additionalDetails = new HashMap<String, String>();
    		JSONArray serviceType = context.read("edcrDetail.*.applicationSubType");
    		if (CollectionUtils.isEmpty(serviceType)) {
    			serviceType.add("NEW_CONSTRUCTION");
    		}
    		JSONArray applicationType = context.read("edcrDetail.*.appliactionType");
    		if (StringUtils.isEmpty(applicationType)) {
    			applicationType.add("permit");
    		}
    		additionalDetails.put("serviceType", serviceType.get(0).toString());
    		additionalDetails.put("applicationType", applicationType.get(0).toString());
            
    		
            log.debug("applicationType is " + additionalDetails.get("applicationType"));
            log.debug("serviceType is " + additionalDetails.get("serviceType"));
            String filterExp = "$.[?((@.applicationType == '"+ additionalDetails.get("applicationType")+"' || @.applicationType === 'ALL' ) &&  @.feeType == '"+feeType+"')]";
            List<Object> calTypes = JsonPath.read(jsonOutput, filterExp);
            
            filterExp = "$.[?(@.serviceType == '"+ additionalDetails.get("serviceType")+"' || @.serviceType === 'ALL' )]";
            calTypes = JsonPath.read(calTypes, filterExp);
            
            filterExp = "$.[?(@.riskType == '"+bpa.getRiskType()+"' || @.riskType === 'ALL' )]";
            calTypes = JsonPath.read(calTypes, filterExp);
            
            if(calTypes.size() > 1){
	            	filterExp = "$.[?(@.riskType == '"+bpa.getRiskType()+"' )]";
	            	calTypes  = JsonPath.read(calTypes, filterExp);
            }
            
            if(calTypes.size() == 0) {
            		return defaultMap(feeType);
            }
            
             Object obj = calTypes.get(0);
           
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            
           String financialYear = currentYear + "-" + (currentYear + 1);
           System.out.println(financialYear);
            
            calculationType = (HashMap<String, Object>) obj;
        }
        catch (Exception e){
            throw new CustomException(BPACalculatorConstants.CALCULATION_ERROR, "Failed to get calculationType");
        }

        return calculationType;
    }
   
    /**
     * Creates and return default calculationType values as map
     * @return default calculationType Map
     */	
    private Map defaultMap(String feeType){
        Map defaultMap = new HashMap();
        String feeAmount = ( feeType.equalsIgnoreCase(BPACalculatorConstants.MDMS_CALCULATIONTYPE_APL_FEETYPE) ) ? config.getApplFeeDefaultAmount() : config.getSancFeeDefaultAmount();
        defaultMap.put( BPACalculatorConstants.MDMS_CALCULATIONTYPE_AMOUNT,feeAmount);
        return defaultMap;
    }
    
    /**
     * Gets the startDate and the endDate of the current financialYear
     * @param mdmsData The MDMS data from the mdms call
     * @return Map containing the startDate and endDate
     */
    public Map<String, Object> getTaxPeriods(Object mdmsData){
        Map<String,Object> taxPeriods = new HashMap<>();
        try {
//        	Object mdmsData = util.mDMSCall(requestInfo, criteria.getTenantId() );
//            String jsonPath = TLConstants.MDMS_CURRENT_FINANCIAL_YEAR.replace("{}",businessService_TL);
//            List<Map<String,Object>> jsonOutput =  JsonPath.read(mdmsData, jsonPath);
            String jsonPath = BPACalculatorConstants.MDMS_CURRENT_FINANCIAL_YEAR.replace("{}",businessService_BPA);
            List<Map<String,Object>> jsonOutput =  JsonPath.read(mdmsData, jsonPath);
            
            for (int i=0; i<jsonOutput.size();i++) {
           	 Object startingDate = jsonOutput.get(i).get(BPACalculatorConstants.MDMS_STARTDATE);
           	 Object endingDate = jsonOutput.get(i).get(BPACalculatorConstants.MDMS_ENDDATE);
           	 Object financialYear = jsonOutput.get(i).get(BPACalculatorConstants.MDMS_NAME);
           	 Long startTime = (Long)startingDate;
           	 Long endTime = (Long)endingDate;
           	 
           	 if(System.currentTimeMillis()>=startTime && System.currentTimeMillis()<=endTime) {
           		taxPeriods.put(BPACalculatorConstants.MDMS_STARTDATE, startTime);
                taxPeriods.put(BPACalculatorConstants.MDMS_ENDDATE, endTime);
                taxPeriods.put(BPACalculatorConstants.MDMS_FINANCIALYEAR, financialYear);
           		 break;
           	 }
           	 
            }
        	
        } catch (Exception e) {
            log.error("Error while fetvhing MDMS data", e);
            throw new CustomException("INVALID FINANCIALYEAR", "No financial Year data found for the module : " + businessService_BPA);
        }
        return taxPeriods;
    }
    
    /**
     * Gets the MDMS data for Sanction Fee Charges of BPA
     * @param CalculationReq to retrieve RequestInfo
     * @param tenantId of the BPA
     * @param Charges type code
     * @param BPA category
     * @return MDMS data for Sanction Fee Charges
     */
    public Object getMDMSSanctionFeeCharges (RequestInfo requestInfo, String tenantId, String code,
    		String category, String fromFY) {
    	
    	MdmsCriteriaReq mdmsCriteriaReq = getMDMSSanctionFeeRequest(requestInfo, tenantId, code, category, fromFY);
		StringBuilder url = getMdmsSearchUrl();
		Object result = serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
		return result;
	}

	private MdmsCriteriaReq getMDMSSanctionFeeRequest(RequestInfo requestInfo, String tenantId, String code,
			String category, String fromFY) {
        
        List<MasterDetail> sanctionFeeChargesDetails = new ArrayList<>();
        Long currentTime = System.currentTimeMillis();
        final String filterCodeForCharges = "$.[?(@.active==true && @.code=='" + code + "' && @.Category == '" + category + "' && @.fromFY == '" + fromFY + "' && @.startingDate <= "+ currentTime +" && @.endingDate >= "+ currentTime +" )]";
        sanctionFeeChargesDetails.add(MasterDetail.builder().name(BPACalculatorConstants.MDMS_CHARGES_TYPE).filter(filterCodeForCharges).build());
        ModuleDetail fyModuleDtls = ModuleDetail.builder().masterDetails(sanctionFeeChargesDetails)
                .moduleName(BPACalculatorConstants.MDMS_BPA).build();
        

        List<ModuleDetail> moduleDetails = new ArrayList<>();
        
        moduleDetails.add(fyModuleDtls);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }


}
