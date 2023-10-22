package org.egov.pt.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PetConfiguration;
import org.egov.pt.models.ConstructionDetail;
import org.egov.pt.models.Institution;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.PetApplicationSearchCriteria;
import org.egov.pt.models.PetRegistrationApplication;
import org.egov.pt.models.PetRegistrationRequest;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.Unit;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.workflow.BusinessService;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.models.workflow.State;
import org.egov.pt.repository.PetRegistrationRepository;
import org.egov.pt.service.DiffService;
import org.egov.pt.service.PetRegistrationService;
import org.egov.pt.service.WorkflowService;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {

    @Autowired
    private PetConfiguration configs;
    
    @Autowired
    private PetRegistrationService service;
    
    @Autowired
    private ObjectMapper mapper;
    
    
    @Autowired
    private DiffService diffService;
    
    @Autowired
    private WorkflowService workflowService;
	
	@Autowired
	private PetRegistrationRepository repository;
    /**
     * Validate the masterData and ctizenInfo of the given propertyRequest
     * @param request PropertyRequest for create
     */
    
    public void validatePetApplication(PetRegistrationRequest petRegistrationRequest) {
        petRegistrationRequest.getPetRegistrationApplications().forEach(application -> {
            if(ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("EG_BT_APP_ERR", "tenantId is mandatory for creating pet registration applications");
        });
    }
    public PetRegistrationApplication validateApplicationExistence(PetRegistrationApplication petRegistrationApplication) {
        return repository.getApplications(PetApplicationSearchCriteria.builder().applicationNumber(petRegistrationApplication.getApplicationNumber()).build()).get(0);
    }
	
    /**
     *Checks if the codes of all fields are in the list of codes obtain from master data
     *
     * @param property property from PropertyRequest which are to validated
     * @param codes Map of MasterData name to List of codes in that MasterData
     * @param errorMap Map to fill all errors caught to send as custom Exception
     * @return Error map containing error if existed
     *
     */
    private static Map<String,String> validateCodes(Property property, Map<String,List<String>> codes, Map<String,String> errorMap){
    	
		if (property.getPropertyType() != null && !codes.get(PTConstants.MDMS_PT_PROPERTYTYPE).contains(property.getPropertyType())) {
			errorMap.put("Invalid PROPERTYTYPE", "The PropertyType '" + property.getPropertyType() + "' does not exists");
		}

		if (property.getOwnershipCategory() != null && !codes.get(PTConstants.MDMS_PT_OWNERSHIPCATEGORY).contains(property.getOwnershipCategory())) {
			errorMap.put("Invalid OWNERSHIPCATEGORY", "The OwnershipCategory '" + property.getOwnershipCategory() + "' does not exists");
		}

		if (property.getUsageCategory() != null && !codes.get(PTConstants.MDMS_PT_USAGECATEGORY).contains(property.getUsageCategory())) {
			errorMap.put("Invalid USageCategory", "The USageCategory '" + property.getUsageCategory() + "' does not exists");
		}
		
		if (!CollectionUtils.isEmpty(property.getUnits()))
			for (Unit unit : property.getUnits()) {

				if (ObjectUtils.isEmpty(unit.getUsageCategory()) || unit.getUsageCategory() != null
						&& !codes.get(PTConstants.MDMS_PT_USAGECATEGORY).contains(unit.getUsageCategory())) {
					errorMap.put("INVALID USAGE CATEGORY ", "The Usage CATEGORY '" + unit.getUsageCategory()
							+ "' does not exists for unit of index : " + property.getUnits().indexOf(unit));
				}

				String constructionType = unit.getConstructionDetail().getConstructionType();
				
				if (!ObjectUtils.isEmpty(constructionType)
						&& !codes.get(PTConstants.MDMS_PT_CONSTRUCTIONTYPE).contains(constructionType)) {
					errorMap.put("INVALID CONSTRUCTION TYPE ", "The CONSTRUCTION TYPE '" + constructionType
							+ "' does not exists for unit of index : " + property.getUnits().indexOf(unit));
				}
				
				if (!ObjectUtils.isEmpty(unit.getOccupancyType())
						&& !codes.get(PTConstants.MDMS_PT_OCCUPANCYTYPE).contains(unit.getOccupancyType())) {
					errorMap.put("INVALID OCCUPANCYTYPE TYPE ", "The OCCUPANCYTYPE TYPE '" + unit.getOccupancyType()
							+ "' does not exists for unit of index : " + property.getUnits().indexOf(unit));
				}

			}

		if (!CollectionUtils.isEmpty(errorMap))
			throw new CustomException(errorMap);

		for (OwnerInfo owner : property.getOwners()) {

			if (owner.getOwnerType() != null
					&& !codes.get(PTConstants.MDMS_PT_OWNERTYPE).contains(owner.getOwnerType())) {

				errorMap.put("INVALID OWNERTYPE", "The OwnerType '" + owner.getOwnerType() + "' does not exists");
			}
		}

		if(!CollectionUtils.isEmpty(property.getDocuments()) && property.getDocuments().contains(null))
			errorMap.put("INVALID ENTRY IN PROPERTY DOCS", " The proeprty documents cannot contain null values");
		
		
		

		return errorMap;

	}

    /**
     * Validates if MasterData is properly fetched for the given MasterData names
     * @param masterNames
     * @param codes
     */
    private void validateMDMSData(List<String> masterNames,Map<String,List<String>> codes){
    	
        Map<String,String> errorMap = new HashMap<>();
        for(String masterName:masterNames){
            if(CollectionUtils.isEmpty(codes.get(masterName))){
                errorMap.put("MDMS DATA ERROR ","Unable to fetch "+masterName+" codes from MDMS");
            }
        }
        if (!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


	/**
	 * Validates if the mobileNumber is 10 digit and starts with 5 or greater
	 * 
	 * @param mobileNumber The mobileNumber to be validated
	 * @return True if valid mobileNumber else false
	 */
	private Boolean isMobileNumberValid(String mobileNumber) {

		if (mobileNumber == null)
			return false;
		else if (mobileNumber.length() != 10)
			return false;
		else if (Character.getNumericValue(mobileNumber.charAt(0)) < 5)
			return false;
		else
			return true;
	}

}
