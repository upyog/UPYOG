package org.egov.rl.services.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.services.config.RentLeaseConfiguration;
import org.egov.rl.services.models.Address;
import org.egov.rl.services.models.AllotmentCriteria;
import org.egov.rl.services.models.AllotmentDetails;
import org.egov.rl.services.models.AllotmentRequest;
import org.egov.rl.services.models.AuditDetails;
import org.egov.rl.services.models.Document;
import org.egov.rl.services.models.OwnerInfo;
import org.egov.rl.services.models.enums.Status;
import org.egov.rl.services.util.RentLeaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class AllotmentEnrichmentService {

	@Autowired
	private RentLeaseUtil propertyutil;

	@Autowired
	private RentLeaseConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private AllotmentService allotmentService;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Assigns UUIDs to all id fields and also assigns acknowledgement-number and
	 * assessment-number generated from id-gen
	 * 
	 * @param request PropertyRequest received for property creation
	 */

	public void enrichCreateRequest(AllotmentRequest allotmentRequest) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		RequestInfo requestInfo = allotmentRequest.getRequestInfo();

		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		allotmentDetails.setAuditDetails(auditDetails);
		enrichUuidsForOwnerCreate(requestInfo, allotmentRequest);
		setIdgenIds(allotmentRequest);
		if(allotmentRequest.getAllotment().get(0).getPreviousApplicationNumber()==null) {
		 setRegistrationNumber(allotmentRequest);
		}
		
		enrichTaxApplicability(requestInfo, allotmentDetails);
		enrichAdditionalDetails(allotmentDetails);
	}



	private void enrichTaxApplicability(RequestInfo requestInfo, AllotmentDetails allotmentDetails) {
		try {
			com.fasterxml.jackson.databind.node.ArrayNode taxRates = propertyutil.fetchTaxRatesFromMdms(requestInfo, allotmentDetails.getTenantId());
			if (taxRates != null) {
				for (com.fasterxml.jackson.databind.JsonNode taxRate : taxRates) {
					String taxType = taxRate.path("taxType").asText();
					boolean isActive = taxRate.path("isActive").asBoolean(false);
					if (isActive) {
						if ("RL_CGST_FEE".equals(taxType) || "RL_SGST_FEE".equals(taxType)) {
							allotmentDetails.setGSTApplicable(true);
						}
						if ("RL_COWCESS_FEE".equals(taxType)) {
							allotmentDetails.setCowCessApplicable(true);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error enriching tax applicability from MDMS: " + e.getMessage());
		}
	}

	private void enrichAdditionalDetails(AllotmentDetails allotmentDetails) {
		com.fasterxml.jackson.databind.node.ObjectNode additionalDetailsNode;
		if (allotmentDetails.getAdditionalDetails() != null && allotmentDetails.getAdditionalDetails().isObject()) {
			additionalDetailsNode = (com.fasterxml.jackson.databind.node.ObjectNode) allotmentDetails.getAdditionalDetails();
		} else {
			additionalDetailsNode = mapper.createObjectNode();
		}
		
		if (additionalDetailsNode.has("propertyDetails") && additionalDetailsNode.get("propertyDetails").isArray()) {
			com.fasterxml.jackson.databind.node.ArrayNode propertyDetailsArray = (com.fasterxml.jackson.databind.node.ArrayNode) additionalDetailsNode.get("propertyDetails");
			for (com.fasterxml.jackson.databind.JsonNode propertyNode : propertyDetailsArray) {
				if (propertyNode.isObject()) {
					com.fasterxml.jackson.databind.node.ObjectNode propObj = (com.fasterxml.jackson.databind.node.ObjectNode) propertyNode;
					if (!propObj.has("propertyType") || propObj.get("propertyType").isNull() || propObj.get("propertyType").asText().isEmpty()) {
						propObj.put("propertyType", "Commercial");
					}
				}
			}
		}
		
		allotmentDetails.setAdditionalDetails(additionalDetailsNode);
	}

	/**
	 * Assigns UUID for new fields that are added and sets propertyDetail and
	 * address id from propertyId
	 * 
	 * @param request        PropertyRequest received for property update
	 * @param propertyFromDb Properties returned from DB
	 */
	public void enrichUpdateRequest(AllotmentRequest allotmentRequest) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		RequestInfo requestInfo = allotmentRequest.getRequestInfo();

		AllotmentCriteria allotmentCriteria = new AllotmentCriteria();
		Set<String> id = new HashSet<>();
		id.add(allotmentRequest.getAllotment().get(0).getId());
		allotmentCriteria.setAllotmentIds(id);
		allotmentCriteria.setTenantId(allotmentRequest.getAllotment().get(0).getTenantId());
		
		AllotmentDetails allotmentDbDetails = allotmentService.searchAllotedApplications(allotmentRequest.getRequestInfo(), allotmentCriteria).stream().findFirst().orElse(null);		
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);
		auditDetails.setCreatedBy(allotmentDbDetails.getCreatedBy());
		auditDetails.setCreatedTime(allotmentDbDetails.getCreatedTime());

		allotmentDbDetails.setAuditDetails(auditDetails);
		allotmentDbDetails.setStartDate(allotmentDetails.getStartDate());
		allotmentDbDetails.setEndDate(allotmentDetails.getEndDate());
		allotmentDbDetails.setGSTApplicable(allotmentDetails.isGSTApplicable());
		allotmentDbDetails.setCowCessApplicable(allotmentDetails.isCowCessApplicable());
		allotmentDbDetails.setRefundApplicableOnDiscontinuation(allotmentDetails.isRefundApplicableOnDiscontinuation());
		allotmentDbDetails.setWitnessDetails(allotmentDetails.getWitnessDetails());
		allotmentDbDetails.setTermAndCondition(allotmentDetails.getTermAndCondition());
		allotmentDbDetails.setPenaltyType(allotmentDetails.getPenaltyType());
		allotmentDbDetails.setOwnerInfo(allotmentDetails.getOwnerInfo());
		allotmentDbDetails.setDocuments(allotmentDetails.getDocuments());
		
		allotmentDbDetails.setStatus(allotmentDetails.getWorkflow().getStatus());
		allotmentDbDetails.setWorkflow(allotmentDetails.getWorkflow());
		
		allotmentDbDetails.setAdditionalDetails(allotmentDetails.getAdditionalDetails());
		allotmentDbDetails.setApplicationNumber(allotmentDetails.getApplicationNumber());
		allotmentDbDetails.setPropertyId(allotmentDetails.getPropertyId());
		allotmentDbDetails.setTenantId(allotmentDetails.getTenantId());
		allotmentDbDetails.setAmountToBeDeducted(allotmentDetails.getAmountToBeDeducted());
		allotmentRequest.setAllotment(Arrays.asList(allotmentDbDetails));
		enrichUuidsForOwnerUpdate(requestInfo, allotmentRequest, allotmentDbDetails);

	}

	private void enrichUuidsForOwnerCreate(RequestInfo requestInfo, AllotmentRequest allotmentRequest) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		String allotmentId = UUID.randomUUID().toString();

		List<OwnerInfo> lst = allotmentDetails.getOwnerInfo().stream().map(m -> {
			m.setOwnerId(UUID.randomUUID().toString());
			m.setStatus(Status.ACTIVE);
			m.setAllotmentId(allotmentId);
			return m;
		}).collect(Collectors.toList());
		allotmentDetails.setOwnerInfo(lst);
		allotmentDetails.setId(allotmentId);
		List<AllotmentDetails> allotmentDetails2 = new ArrayList();
		allotmentDetails2.add(allotmentDetails);
		allotmentRequest.setAllotment(Arrays.asList(allotmentDetails));

	}

	private void enrichUuidsForOwnerUpdate(RequestInfo requestInfo, AllotmentRequest allotmentRequest,
			AllotmentDetails allotmentDbDetails) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		AuditDetails auditDbDetails = allotmentDbDetails.getAuditDetails();
		String allotmentId = allotmentDetails.getId();
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);
		auditDetails.setCreatedBy(auditDbDetails.getCreatedBy());
		auditDetails.setCreatedTime(auditDbDetails.getCreatedTime());

		List<OwnerInfo> lst = allotmentDetails.getOwnerInfo().stream().map(m -> {
			m.setOwnerId(m.getOwnerId() == null ? UUID.randomUUID().toString() : m.getOwnerId());
			m.setStatus(Status.ACTIVE);
			m.setAllotmentId(allotmentId);
			return m;
		}).collect(Collectors.toList());
		allotmentDetails.setOwnerInfo(lst);
		allotmentDetails.setId(allotmentId);
		List<AllotmentDetails> allotmentDetails2 = new ArrayList();
		allotmentDetails2.add(allotmentDetails);
		updateDocument(allotmentDetails, allotmentId, auditDetails);
		allotmentRequest.setAllotment(Arrays.asList(allotmentDetails));

	}

	private void updateDocument(AllotmentDetails allotmentDetails, String allotmentId, AuditDetails auditDetails) {
		if (allotmentDetails.getDocuments() != null && allotmentDetails.getDocuments().size() > 0) {
			List<Document> docList = allotmentDetails.getDocuments().stream().map(doc -> {
				Document document = doc;
				document.setDocumentUid(allotmentId);
				document.setId(doc.getId() == null ? UUID.randomUUID().toString() : doc.getId());
				document.setStatus(Status.ACTIVE);
				document.setAuditDetails(auditDetails);
				return document;
			}).collect(Collectors.toList());
			allotmentDetails.setDocuments(docList);
		}
	}

	/**
	 * Sets the acknowledgement and assessment Numbers for given PropertyRequest
	 * 
	 * @param request PropertyRequest which is to be created
	 */
	private void setIdgenIds(AllotmentRequest allotmentRequest) {

		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		String tenantId = allotmentDetails.getTenantId();
		RequestInfo requestInfo = allotmentRequest.getRequestInfo();

		if (config.getIsWorkflowEnabled()) {
			allotmentDetails.setStatus(allotmentRequest.getAllotment().get(0).getWorkflow().getStatus());
		}
		String applicationNumber = propertyutil.getIdList(requestInfo, tenantId,
				config.getAllotmentApplicationNummberGenName(), config.getAllotmentApplicationNummberGenNameFormat(), 1)
				.get(0);
		allotmentDetails.setApplicationNumber(applicationNumber);
		List<AllotmentDetails> allotmentDetails2 = new ArrayList();
		allotmentDetails2.add(allotmentDetails);
		allotmentRequest.setAllotment(Arrays.asList(allotmentDetails));

	}

	private void setRegistrationNumber(AllotmentRequest allotmentRequest) {

		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		String tenantId = allotmentDetails.getTenantId();
		RequestInfo requestInfo = allotmentRequest.getRequestInfo();
		String registrationNumber = propertyutil.getIdList(requestInfo, tenantId,
				config.getRlRegistrationNumber(), config.getRlRegistrationNumberFormat(), 1)
				.get(0);
		allotmentDetails.setRegistrationNumber(registrationNumber);
		List<AllotmentDetails> allotmentDetails2 = new ArrayList();
		allotmentDetails2.add(allotmentDetails);
		allotmentRequest.setAllotment(Arrays.asList(allotmentDetails));

	}

	public void enrichOwnerDetailsFromUserService(List<AllotmentDetails> allotmentDetails, RequestInfo requestInfo) {
		allotmentDetails.stream().map(d -> {
			return d.getOwnerInfo().stream().map(ownerInfo -> {
				try {
					// Fetch user details from user service using the stored UUID
					org.egov.rl.services.models.user.UserSearchRequest userSearchRequest = userService
							.getBaseUserSearchRequest(d.getTenantId(), requestInfo);
					userSearchRequest.setUuid(java.util.Collections.singleton(ownerInfo.getUserUuid()));

					org.egov.rl.services.models.user.UserDetailResponse userDetailResponse = userService
							.getUser(userSearchRequest);

					if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
						org.egov.rl.services.models.user.User user = userDetailResponse.getUser().get(0);
						ownerInfo.setUserUuid(user.getUuid());
//						ownerInfo.setUserName(user.getUserName());
						ownerInfo.setName(user.getName());
						ownerInfo.setGender(user.getGender());
						ownerInfo.setMobileNo(user.getMobileNumber());
						ownerInfo.setEmailId(user.getEmailId());
						
						ownerInfo.setPermanentAddress(Address.builder().addressId(user.getPermanentAddress())
								.pincode(user.getPermanentPincode()).addressLine1(null).city(user.getPermanentCity())
								.build());
						ownerInfo.setCorrespondenceAddress(Address.builder().addressId(user.getCorrespondenceAddress())
								.pincode(user.getCorrespondencePincode()).addressLine1(null)
								.city(user.getCorrespondenceCity()).build());
						ownerInfo.setActive(user.getActive());
						ownerInfo.setDob(user.getDob());
						ownerInfo.setLocale(user.getLocale());
						ownerInfo.setType(user.getType());
						ownerInfo.setRoles(user.getRoles());
						ownerInfo.setFatherOrHusbandName(user.getFatherOrHusbandName());
						ownerInfo.setTenantId(user.getTenantId());
					}
				} catch (Exception e) {
					// Log error but don't fail the search
					System.err.println("Error fetching user details for owner: " + ownerInfo.getUserUuid() + ", Error: "
							+ e.getMessage());
				}
				return ownerInfo;
			}).collect(Collectors.toList());
		}).collect(Collectors.toList());
	}
}
