package org.egov.pt.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PetConfiguration;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.Institution;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.PetRegistrationApplication;
import org.egov.pt.models.PetRegistrationRequest;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.User;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
public class EnrichmentService {


    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private PetConfiguration config;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PropertyUtil petUtil;
	



    /**
     * Assigns UUIDs to all id fields and also assigns acknowledgement-number and assessment-number generated from id-gen
     * @param request  PropertyRequest received for property creation
     */
	public void enrichPetApplication(PetRegistrationRequest petRegistrationRequest) {
		 //List<String> petRegistrationIdList = idgenUtil.getIdList(petRegistrationRequest.getRequestInfo(), petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId(), "ptr.registrationid", "", petRegistrationRequest.getPetRegistrationApplications().size());
		 List<String> petRegistrationIdList = petUtil.getIdList(petRegistrationRequest.getRequestInfo(), petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId(),config.getPetIdGenName(), config.getPetIdGenFormat(), petRegistrationRequest.getPetRegistrationApplications().size());     
		 Integer index = 0; 
		        for(PetRegistrationApplication application : petRegistrationRequest.getPetRegistrationApplications()){
		            
		            AuditDetails auditDetails = AuditDetails.builder().createdBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
		            application.setAuditDetails(auditDetails); // Enrich audit details
		            application.setId(UUID.randomUUID().toString()); // Enrich UUID
		            application.getAddress().setRegistrationId(application.getId()); // Enrich registration Id
		            application.getAddress().setId(UUID.randomUUID().toString()); // Enrich address UUID
		            application.getPetDetails().setPetDetailsId(application.getId());
		            application.getPetDetails().setId(UUID.randomUUID().toString()); // Enrich petDetails UUID
		            application.setApplicationNumber(petRegistrationIdList.get(index++)); //Enrich application number from IDgen
		            if (!CollectionUtils.isEmpty(application.getDocuments()))
		            	application.getDocuments().forEach(doc -> {
		    				if (doc.getId() == null) {
		    					doc.setId(UUID.randomUUID().toString());
		    					doc.setActive(true);
		    					doc.setTenantId(application.getTenantId());
		    					doc.setAuditDetails(application.getAuditDetails());
		    					
		    				}
		    				System.out.println("Document id:"+application.getDocuments());
		    			});
		            
		           // application.setApplicationNumber(UUID.randomUUID().toString());
		        }
		    }
	
	  public void enrichPetApplicationUponUpdate(PetRegistrationRequest petRegistrationRequest) {
	        // Enrich lastModifiedTime and lastModifiedBy in case of update
	        petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
	        petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails().setLastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
	    }
	
	




    /**
	 * Sets the acknowledgement and assessment Numbers for given PropertyRequest
	 * 
	 * @param request PropertyRequest which is to be created
	 */
	private void setIdgenIds(PropertyRequest request) {

		Property property = request.getProperty();
		String tenantId = property.getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		if (!config.getIsWorkflowEnabled()) {

			property.setStatus(Status.ACTIVE);
		}
		
		String pId = petUtil.getIdList(requestInfo, tenantId, config.getPropertyIdGenName(), config.getPropertyIdGenFormat(), 1).get(0);
		String ackNo = petUtil.getIdList(requestInfo, tenantId, config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);
		property.setPropertyId(pId);
		property.setAcknowldgementNumber(ackNo);
	}


    /**
     * Returns PropertyCriteria with ids populated using propertyids from properties
     * @param properties properties whose propertyids are to added to propertyCriteria for search
     * @return propertyCriteria to search on basis of propertyids
     */
	public PropertyCriteria getPropertyCriteriaFromPropertyIds(List<Property> properties) {

		PropertyCriteria criteria = new PropertyCriteria();
		Set<String> propertyids = new HashSet<>();
		properties.forEach(property -> propertyids.add(property.getPropertyId()));
		criteria.setPropertyIds(propertyids);
		criteria.setTenantId(properties.get(0).getTenantId());
		return criteria;
	}

    /**
     *  Enriches the locality object
     * @param property The property object received for create or update
     */
    public void enrichBoundary(Property property, RequestInfo requestInfo){
    	
        boundaryService.getAreaType(property, requestInfo, PTConstants.BOUNDARY_HEIRARCHY_CODE);
    }
    
    
	
	
    public void enrichAssignes(Property property){

            if(config.getIsWorkflowEnabled() && property.getWorkflow().getAction().equalsIgnoreCase(PTConstants.CITIZEN_SENDBACK_ACTION)){

                    List<User> assignes = new LinkedList<>();

                    // Adding owners to assignes list
                    property.getOwners().forEach(ownerInfo -> {
                       assignes.add(ownerInfo);
                    });

                    // Adding creator of application
                    if(property.getAccountId()!=null)
                        assignes.add(OwnerInfo.builder().uuid(property.getAccountId()).build());

					Set<User> registeredUsers = userService.getUUidFromUserName(property);

					if(!CollectionUtils.isEmpty(registeredUsers))
						assignes.addAll(registeredUsers);

                    property.getWorkflow().setAssignes(assignes);
            }
    }


}
