package org.egov.filemgmnt.validators;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.filemgmnt.repository.ServiceRequestRepository;

import org.egov.filemgmnt.util.FMConstants;
 
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

 

import java.util.*;


@Component
@Slf4j
public class MDMSValidator {
	
	private ServiceRequestRepository requestRepository;
   

    private ServiceRequestRepository serviceRequestRepository;


    @Autowired
    public MDMSValidator(ServiceRequestRepository requestRepository, 
                         ServiceRequestRepository serviceRequestRepository) {
        this.requestRepository = requestRepository;
       
        this.serviceRequestRepository = serviceRequestRepository;
    }

	
	  /**
     * method to validate the mdms data in the request
     *
     * @param licenseRequest
     */
    public void validateMdmsData(ApplicantPersonalRequest request,Object mdmsData) {

    
        Map<String, String> errorMap = new HashMap<>();
        
        Map<String, List<String>> masterData = getAttributeValues(mdmsData);
        
        String[] masterArray = {FMConstants.FILE_SERVICE_SUBTYPE};
        
        validateIfMasterPresent(masterArray, masterData);       

        request.getApplicantPersonals().forEach(personal -> {   
        	
//        	if(masterData.get(FMConstants.FILE_SERVICE_SUBTYPE)
//        			.contains(personal.getServiceDetails().getServiceCode())) {
//               errorMap.put("FileServiceSubtype", "The Service SubType '"
//                                + personal.getServiceDetails().getServiceCode()+ "' does not exists");
//        	}
//        	else {
//        		 errorMap.put("error","");
//        	}
        	
        	if(!masterData.get(FMConstants.FILE_SERVICE_SUBTYPE)
        			.contains(personal.getServiceDetails().getServiceCode())) {
               errorMap.put("FileServiceSubtype", "The Service SubType '"
                                + personal.getServiceDetails().getServiceCode()+ "' does not exists");
        	}
            });
        
        log.info("errorMap   :"+errorMap );        
        log.info("service subtype   :"  +masterData.get(FMConstants.FILE_SERVICE_SUBTYPE));
        log.info("getServiceCode   :"  + request.getApplicantPersonals().get(0).getServiceDetails().getServiceCode());
        if (!CollectionUtils.isEmpty(errorMap)) 
            throw new CustomException(errorMap);       
        	
    }

    
    /**
     * Fetches all the values of particular attribute as map of field name to list
     *
     * takes all the masters from each module and adds them in to a single map
     *
     * note : if two masters from different modules have the same name then it
     *
     *  will lead to overriding of the earlier one by the latest one added to the map
     *
     * @return Map of MasterData name to the list of code in the MasterData
     *
     */
    private Map<String, List<String>> getAttributeValues(Object mdmsData) {

        List<String> modulepaths = Arrays.asList(FMConstants.FM_JSONPATH_CODE);
        		 

        final Map<String, List<String>> mdmsResMap = new HashMap<>();
        modulepaths.forEach( modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
            } catch (Exception e) {
                log.error("Error while fetvhing MDMS data", e);
                throw new CustomException(FMConstants.INVALID_TENANT_ID_MDMS_KEY, FMConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
        });

        System.err.println(" the mdms response is : " + mdmsResMap);
        return mdmsResMap;
    }
    
    /**
     * Validates if MasterData is properly fetched for the given MasterData names
     * @param masterNames
     * @param codes
     */
    private void validateIfMasterPresent(String[] masterNames,Map<String,List<String>> codes){
        Map<String,String> errorMap = new HashMap<>();
        for(String masterName:masterNames){
            if(CollectionUtils.isEmpty(codes.get(masterName))){
                errorMap.put("MDMS DATA ERROR ","Unable to fetch "+masterName+" codes from MDMS");
            }
        }
        if (!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }
}
