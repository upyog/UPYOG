package org.egov.pt.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.Institution;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBillFailure;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.Unit;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.enums.BillStatus;
import org.egov.pt.models.enums.Status;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.PtTaxCalculatorTrackerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EnrichmentService {


    @Autowired
    private PropertyUtil propertyutil;

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private PropertyConfiguration config;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ObjectMapper objectMapper;


    /**
     * Assigns UUIDs to all id fields and also assigns acknowledgement-number and assessment-number generated from id-gen
     * @param request  PropertyRequest received for property creation
     */
	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		Property property = request.getProperty();
		
		property.setIsBilling(request.getProperty().getIsBilling());
		
		property.setAccountId(requestInfo.getUserInfo().getUuid());
		enrichUuidsForPropertyCreate(requestInfo, property);
		setIdgenIds(request);
//		enrichBoundary(property, requestInfo);
	}

	private void enrichUuidsForPropertyCreate(RequestInfo requestInfo, Property property) {
		
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		property.setId(UUID.randomUUID().toString());
		
		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {
				doc.setId(UUID.randomUUID().toString());
				doc.setStatus(Status.ACTIVE);
			});

		property.getAddress().setTenantId(property.getTenantId());
		property.getAddress().setId(UUID.randomUUID().toString());

		if (!ObjectUtils.isEmpty(property.getInstitution()))
			property.getInstitution().setId(UUID.randomUUID().toString());

		property.setAuditDetails(propertyAuditDetails);
		
		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				unit.setId(UUID.randomUUID().toString());
				unit.setActive(true);
			});
		
		property.getOwners().forEach(owner -> {
			
			owner.setOwnerInfoUuid(UUID.randomUUID().toString());
			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					doc.setId(UUID.randomUUID().toString());
					doc.setStatus(Status.ACTIVE);
				});
			
			owner.setStatus(Status.ACTIVE);
		});
	}

    /**
     * Assigns UUID for new fields that are added and sets propertyDetail and address id from propertyId
     * 
     * @param request  PropertyRequest received for property update
     * @param propertyFromDb Properties returned from DB
     */
    public void enrichUpdateRequest(PropertyRequest request,Property propertyFromDb) {
    	
    	Property property = request.getProperty();
        RequestInfo requestInfo = request.getRequestInfo();
        AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
        propertyFromDb.setAuditDetails(auditDetailsForUpdate);
        propertyFromDb.setIsBilling(property.getIsBilling());     
        
		Boolean isWfEnabled = config.getIsWorkflowEnabled();
		Boolean iswfStarting = propertyFromDb.getStatus().equals(Status.ACTIVE);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);
			property.getAddress().setId(propertyFromDb.getAddress().getId());

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForNewWf(requestInfo, property, false);
		}
		
		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {

				if (doc.getId() == null) {
					doc.setId(UUID.randomUUID().toString());
					doc.setStatus(Status.ACTIVE);
				}
			});
				
	    	if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				if (unit.getId() == null) {
					unit.setId(UUID.randomUUID().toString());
				}

			});
	    	
	    	
	    	
	    	if (!CollectionUtils.isEmpty(propertyFromDb.getUnits())) {

	    	    // Step 1: Get all unit IDs from frontend (property)
	    	    Set<String> updatedUnitIds = property.getUnits().stream()
	    	        .filter(unit -> unit.getId() != null)
	    	        .map(Unit::getId)
	    	        .collect(Collectors.toSet());

	    	    // Step 2: Deactivate units from DB that are not in the frontend data
	    	    List<Unit> deactivatedUnits = new ArrayList<>();
	    	    
	    	    propertyFromDb.getUnits().forEach(dbUnit -> {
	    	    	
	    	        if (dbUnit.getId() != null && !updatedUnitIds.contains(dbUnit.getId())) {
	    	            dbUnit.setActive(false);
	    	            deactivatedUnits.add(dbUnit); // collect for Kafka payload
	    	        }
	    	    });

	    	    // Step 3: Add deactivated units to the property request object
	    	    property.getUnits().addAll(deactivatedUnits);
	    	}

				
		Institution institute = property.getInstitution();
		if (!ObjectUtils.isEmpty(institute) && null == institute.getId())
			property.getInstitution().setId(UUID.randomUUID().toString());

		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		property.setAuditDetails(auditDetails);
		property.setAccountId(propertyFromDb.getAccountId());
       
		property.setAdditionalDetails(
				propertyutil.jsonMerge(propertyFromDb.getAdditionalDetails(), property.getAdditionalDetails()));
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
		
		String pId = propertyutil
				.getIdList(requestInfo, tenantId, config.getPropertyIdGenName(), config.getPropertyIdGenFormat(), 1)
				.get(0);
		String replaceString = request.getProperty().getAddress().getDistrict() + "/" + objectMapper
				.valueToTree(request.getProperty().getAddress().getAdditionalDetails()).get("ulbName").asText();
		pId = pId.replace("REPLACESTRING", replaceString.toUpperCase());
//		String ackNo = propertyutil
//				.getIdList(requestInfo, tenantId, config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);
		property.setPropertyId(pId);
		property.setAcknowldgementNumber(pId);
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
    
    /**
     * 
     * Enrichment method for mutation request
     * 
     * @param request
     */
	public void enrichMutationRequest(PropertyRequest request, Property propertyFromSearch) {

		RequestInfo requestInfo = request.getRequestInfo();
		Property property = request.getProperty();
		Boolean isWfEnabled = config.getIsMutationWorkflowEnabled();
		Boolean iswfStarting = propertyFromSearch.getStatus().equals(Status.ACTIVE);
		AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		propertyFromSearch.setAuditDetails(auditDetailsForUpdate);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForNewWf(requestInfo, property, true);
		}

		property.getOwners().forEach(owner -> {

			if (owner.getOwnerInfoUuid() == null) {
				
				owner.setOwnerInfoUuid(UUID.randomUUID().toString());
				owner.setStatus(Status.ACTIVE);
			}

			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					if (doc.getId() == null) {
						doc.setId(UUID.randomUUID().toString());
						doc.setStatus(Status.ACTIVE);
					}
				});
		});
		 AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		 property.setAuditDetails(auditDetails);
	}

	/**
	 * enrich property as new entry for workflow validation
	 * 
	 * @param requestInfo
	 * @param property
	 */
	private void enrichPropertyForNewWf(RequestInfo requestInfo, Property property, Boolean isMutation) {
		
		String ackNo;

		if (isMutation) {
			ackNo = propertyutil.getIdList(requestInfo, property.getTenantId(), config.getMutationIdGenName(), config.getMutationIdGenFormat(), 1).get(0);
		} else
			ackNo = propertyutil.getIdList(requestInfo, property.getTenantId(), config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);
		property.setId(UUID.randomUUID().toString());
		property.setAcknowldgementNumber(ackNo);
		
		enrichUuidsForNewUpdate(requestInfo, property);
	}
	
	private void enrichUuidsForNewUpdate(RequestInfo requestInfo, Property property) {
		
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		property.setId(UUID.randomUUID().toString());
		
		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {
				doc.setId(UUID.randomUUID().toString());
				if (null == doc.getStatus())
					doc.setStatus(Status.ACTIVE);
			});

		property.getAddress().setTenantId(property.getTenantId());
		property.getAddress().setId(UUID.randomUUID().toString());

		if (!ObjectUtils.isEmpty(property.getInstitution()))
			property.getInstitution().setId(UUID.randomUUID().toString());

		property.setAuditDetails(propertyAuditDetails);
		
		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				unit.setId(UUID.randomUUID().toString());
				unit.setActive(true);
			});
		
		property.getOwners().forEach(owner -> {
			
			owner.setOwnerInfoUuid(UUID.randomUUID().toString());
			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					doc.setId(UUID.randomUUID().toString());
					if (null == doc.getStatus())
						doc.setStatus(Status.ACTIVE);
				});
			if (null == owner.getStatus())
				owner.setStatus(Status.ACTIVE);
		});
	}
	
    /**
     * In case of SENDBACKTOCITIZEN enrich the assignee with the owners and creator of property
     * @param property to be enriched
     */
    public void enrichAssignes(Property property){

            if(config.getIsWorkflowEnabled() && property.getWorkflow().getAction().equalsIgnoreCase(PTConstants.CITIZEN_SENDBACK_ACTION)){

                    List<OwnerInfo> assignes = new LinkedList<>();

                    // Adding owners to assignes list
                    property.getOwners().forEach(ownerInfo -> {
                       assignes.add(ownerInfo);
                    });

                    // Adding creator of application
                    if(property.getAccountId()!=null)
                        assignes.add(OwnerInfo.builder().uuid(property.getAccountId()).build());

					Set<OwnerInfo> registeredUsers = userService.getUUidFromUserName(property);

					if(!CollectionUtils.isEmpty(registeredUsers))
						assignes.addAll(registeredUsers);

                    property.getWorkflow().setAssignes(assignes);
            }
    }
    
	public PtTaxCalculatorTrackerRequest enrichTaxCalculatorTrackerCreateRequest(Property property,
			CalculateTaxRequest calculateTaxRequest, BigDecimal finalPropertyTax, JsonNode additionalDetails,
			List<Bill> bills, BigDecimal rebateAmount, BigDecimal propertyTaxWithoutRebate) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		AuditDetails createAuditDetails = propertyutil
				.getAuditDetails(calculateTaxRequest.getRequestInfo().getUserInfo().getUuid().toString(), true);
		Bill bill = bills.stream().findFirst().orElse(null);
		PtTaxCalculatorTracker ptTaxCalculatorTracker = PtTaxCalculatorTracker.builder()
				.uuid(UUID.randomUUID().toString()).propertyId(property.getPropertyId())
				.tenantId(property.getTenantId()).financialYear(calculateTaxRequest.getFinancialYear())
				.fromDate(calculateTaxRequest.getFromDate()).toDate(calculateTaxRequest.getToDate())
				.fromDateString(formatter.format(calculateTaxRequest.getFromDate()))
				.toDateString(formatter.format(calculateTaxRequest.getToDate())).propertyTax(finalPropertyTax)
				.additionalDetails(additionalDetails).auditDetails(createAuditDetails)
				.billId(null != bill ? bill.getId() : null).rebateAmount(rebateAmount)
				.type(calculateTaxRequest.getType())
				.propertyTaxWithoutRebate(propertyTaxWithoutRebate).billStatus(BillStatus.ACTIVE).build();

		return PtTaxCalculatorTrackerRequest.builder().requestInfo(calculateTaxRequest.getRequestInfo())
				.ptTaxCalculatorTracker(ptTaxCalculatorTracker).build();
	}

	public PtTaxCalculatorTrackerRequest enrichTaxCalculatorTrackerUpdateRequest(
			PtTaxCalculatorTracker ptTaxCalculatorTracker, RequestInfo requestInfo) {
		AuditDetails updateAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(),
				false);
		updateAuditDetails.setCreatedBy(ptTaxCalculatorTracker.getAuditDetails().getCreatedBy());
		updateAuditDetails.setCreatedTime(ptTaxCalculatorTracker.getAuditDetails().getCreatedTime());
		ptTaxCalculatorTracker.setAuditDetails(updateAuditDetails);

		return PtTaxCalculatorTrackerRequest.builder().requestInfo(requestInfo)
				.ptTaxCalculatorTracker(ptTaxCalculatorTracker).build();
	}
	
	public PropertyBillFailure enrichPtBillFailure(Property property,CalculateTaxRequest generateBillRequest, BillResponse billResponse,Set<String> errorMap) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode response_payload = mapper.valueToTree(billResponse);
		JsonNode request_payload = mapper.valueToTree(generateBillRequest);
		JsonNode error_json = mapper.valueToTree(errorMap);
		String failure_reason = null;
		if(!CollectionUtils.isEmpty(errorMap)) 
			failure_reason = "PROPERTY CONFIGURATION ISSUE";
		else
//			(billResponse == null) 
			failure_reason = "ISSUE WITH BILLING SERVICE";
		PropertyBillFailure ptBillFailure = PropertyBillFailure.builder()
							.consumer_code(property.getPropertyId())
							.tenant_id(property.getTenantId())
							.from_date(null != generateBillRequest.getFromDate() ? dateFormat.format(generateBillRequest.getFromDate()): null)
							.id(UUID.randomUUID())
							.module_name("PROPERTY")
							.failure_reason(failure_reason)
							.error_json(error_json)
							.request_payload(request_payload)
							.response_payload(response_payload)
							.status_code("400")
							.created_time(new Date().getTime())
							.last_modified_time(new Date().getTime())
							.to_date(null != generateBillRequest.getToDate() ? dateFormat.format(generateBillRequest.getToDate()): null)
							.year(generateBillRequest.getFinancialYear()).build();
		return ptBillFailure;
	}

}
