package org.egov.advertisementcanopy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.contract.workflow.BusinessServiceResponse;
import org.egov.advertisementcanopy.contract.workflow.State;
import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteCountData;
import org.egov.advertisementcanopy.model.SiteCountRequest;
import org.egov.advertisementcanopy.model.SiteCountResponse;
import org.egov.advertisementcanopy.model.SiteCreationActionRequest;
import org.egov.advertisementcanopy.model.SiteCreationActionResponse;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteCreationDetail;
import org.egov.advertisementcanopy.model.SiteCreationRequest;
import org.egov.advertisementcanopy.model.SiteCreationResponse;
import org.egov.advertisementcanopy.model.SiteSearchCriteria;
import org.egov.advertisementcanopy.model.SiteSearchRequest;
import org.egov.advertisementcanopy.model.SiteSearchResponse;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.model.SiteUpdationResponse;
import org.egov.advertisementcanopy.producer.Producer;
import org.egov.advertisementcanopy.repository.SiteRepository;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.egov.advertisementcanopy.util.SiteConstants;
import org.egov.advertisementcanopy.model.AllSiteCountResponse;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SiteService {

	@Autowired
	SiteRepository siteRepository;

	@Autowired
	ResponseInfoFactory responseInfoFactory;

	@Autowired
	Producer producer;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	AdvtConstants advtConstants;

	@Autowired
	ObjectMapper objectMapper;

	public static final String ADVERTISEMENT_HOARDING = "Advertising Hoarding";
	public static final String CANOPY = "Canopy";

	public SiteCreationResponse create(SiteCreationRequest createSiteRequest) {
		// Validate Site Duplicacy
		SiteCreationResponse siteCreationResponse = null;
		List<SiteCreationData> ids = new ArrayList<>();
		try {
			if ((null != createSiteRequest.getCreationData().getSiteName()
					&& !createSiteRequest.getCreationData().getSiteName().isEmpty())
					&& (null != createSiteRequest.getCreationData().getDistrictName()
							&& !createSiteRequest.getCreationData().getDistrictName().isEmpty())
					&& (null != createSiteRequest.getCreationData().getUlbName()
							&& !createSiteRequest.getCreationData().getUlbName().isEmpty())
					&& (null != createSiteRequest.getCreationData().getWardNumber()
							&& !createSiteRequest.getCreationData().getWardNumber().isEmpty())) {
				ids = siteRepository.searchSiteIds(createSiteRequest.getCreationData().getSiteName(),
						createSiteRequest.getCreationData().getDistrictName(),
						createSiteRequest.getCreationData().getUlbName(),
						createSiteRequest.getCreationData().getWardNumber());
			}
			if (!CollectionUtils.isEmpty(ids)) {
				throw new RuntimeException("Site already exists...Duplicate Site!!!");
			}
			// enrich Site
			enrichSiteWhileCreation(createSiteRequest);
			State state = workflowService.createWorkflowStatus(createSiteRequest);
			createSiteRequest.getCreationData().setWorkFlowStatus(state.getApplicationStatus());
			// Create Sites
			createSiteObjects(createSiteRequest);
						
			siteCreationResponse = SiteCreationResponse.builder()
					.responseInfo(responseInfoFactory
							.createResponseInfoFromRequestInfo(createSiteRequest.getRequestInfo(), false))
					.creationData(createSiteRequest.getCreationData()).build();
			if (null != siteCreationResponse) {
				siteCreationResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(createSiteRequest.getRequestInfo(), true));
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return siteCreationResponse;

	}

	private void enrichSiteWhileCreation(SiteCreationRequest createSiteRequest) {
		try {
			AuditDetails auditDetails = null;
			if (null != createSiteRequest.getRequestInfo().getUserInfo()) {
				auditDetails = AuditDetails.builder()
						.createdBy(createSiteRequest.getRequestInfo().getUserInfo().getUuid())
						.createdDate(new Date().getTime())
						.lastModifiedBy(createSiteRequest.getRequestInfo().getUserInfo().getUuid())
						.lastModifiedDate(new Date().getTime()).build();
			}

			createSiteRequest.getCreationData().setAuditDetails(auditDetails);
			createSiteRequest.getCreationData().setId(siteRepository.getNextSequence());
			createSiteRequest.getCreationData()
					.setAccountId(createSiteRequest.getRequestInfo().getUserInfo().getUuid());
			if (createSiteRequest.getCreationData().getSiteType().equals(AdvtConstants.ADVERTISEMENT_HOARDING)) {
				createSiteRequest.getCreationData()
						.setSiteID("AHS" + "/" + createSiteRequest.getCreationData().getUlbName() + "/"
								+ siteRepository.getNextSiteSequence());
				createSiteRequest.getCreationData()
						.setSiteName("AHS" + "_" + createSiteRequest.getCreationData().getDistrictName() + "_"
								+ createSiteRequest.getCreationData().getUlbName() + "_"
								+ createSiteRequest.getCreationData().getWardNumber() + "_"
								+ createSiteRequest.getCreationData().getSiteName());
			}
			if (createSiteRequest.getCreationData().getSiteType().equals(AdvtConstants.CANOPY)) {
				createSiteRequest.getCreationData()
						.setSiteID("ACS" + "/" + createSiteRequest.getCreationData().getUlbName() + "/"
								+ siteRepository.getNextSiteSequence());
				createSiteRequest.getCreationData()
						.setSiteName("ACS" + "_" + createSiteRequest.getCreationData().getDistrictName() + "_"
								+ createSiteRequest.getCreationData().getUlbName() + "_"
								+ createSiteRequest.getCreationData().getWardNumber() + "_"
								+ createSiteRequest.getCreationData().getSiteName());
			}
			createSiteRequest.getCreationData().setUuid(UUID.randomUUID().toString());

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private void createSiteObjects(SiteCreationRequest createSiteRequest) {
		// siteRepository.create(createSiteRequest.getCreationData());
		producer.push(AdvtConstants.SITE_CREATION, createSiteRequest);
	}

	public SiteUpdationResponse update(SiteUpdateRequest updateSiteRequest) {
		SiteUpdationResponse siteupdationResponse = null;
		List<SiteCreationData> list = new ArrayList<>();
		List<SiteCreationData> list1 = new ArrayList<>();
		try {
			if (null != updateSiteRequest.getSiteUpdationData()) {
				list = siteRepository.searchSites(updateSiteRequest.getSiteUpdationData());
				list1 = objectMapper.convertValue(list, new TypeReference<List<SiteCreationData>>() {});
			}
			if (!CollectionUtils.isEmpty(list1)) {
				enrichUpdatedSite(updateSiteRequest);
				State state = workflowService.createWorkflowStatusForUpdate(updateSiteRequest);
				updateSiteRequest.getSiteUpdationData().setWorkFlowStatus(state.getApplicationStatus());
				if(StringUtils.equalsIgnoreCase(state.getApplicationStatus(), AdvtConstants.STATUS_APPROVED)) {
					updateSiteRequest.getSiteUpdationData().setActive(true);
				}
				updateExistingSiteData(updateSiteRequest, list1);
				updateSiteData(updateSiteRequest);
			} else {
				throw new RuntimeException("No Site exists with the Details Provided!!!");
			}
			siteupdationResponse = SiteUpdationResponse.builder()
					.responseInfo(responseInfoFactory
							.createResponseInfoFromRequestInfo(updateSiteRequest.getRequestInfo(), false))
					.siteCreationData(updateSiteRequest.getSiteUpdationData()).build();
			if (null != siteupdationResponse) {
				siteupdationResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(updateSiteRequest.getRequestInfo(), true));
			}
 
		} catch (Exception e) {
			throw new RuntimeException("Details provided for Site Updation are invalid!!!");
		}
 
		return siteupdationResponse;
	}

	private void updateExistingSiteData(SiteUpdateRequest updateSiteRequest, List<SiteCreationData> list) {
		try {
			if (updateSiteRequest.getSiteUpdationData().getIsOnlyWorkflowCall()) {
				list.forEach(i -> {
					updateSiteRequest.getSiteUpdationData().setSiteDescription(i.getSiteDescription());
					updateSiteRequest.getSiteUpdationData().setGpsLocation(i.getGpsLocation());
					updateSiteRequest.getSiteUpdationData().setSiteAddress(i.getSiteAddress());
					updateSiteRequest.getSiteUpdationData().setSiteCost(i.getSiteCost());
					updateSiteRequest.getSiteUpdationData().setSitePhotograph(i.getSitePhotograph());
					updateSiteRequest.getSiteUpdationData().setStructure(i.getStructure());
					updateSiteRequest.getSiteUpdationData().setSizeLength(i.getSizeLength());
					updateSiteRequest.getSiteUpdationData().setSizeWidth(i.getSizeWidth());
					updateSiteRequest.getSiteUpdationData().setLedSelection(i.getLedSelection());
					updateSiteRequest.getSiteUpdationData().setSecurityAmount(i.getSecurityAmount());
					updateSiteRequest.getSiteUpdationData().setPowered(i.getPowered());
					updateSiteRequest.getSiteUpdationData().setOthers(i.getOthers());
					updateSiteRequest.getSiteUpdationData().setDistrictName(i.getDistrictName());
					updateSiteRequest.getSiteUpdationData().setUlbName(i.getUlbName());
					updateSiteRequest.getSiteUpdationData().setWardNumber(i.getWardNumber());
					updateSiteRequest.getSiteUpdationData().setPinCode(i.getPinCode());
					updateSiteRequest.getSiteUpdationData().setAdditionalDetail(i.getAdditionalDetail());
					updateSiteRequest.getSiteUpdationData().setSiteType(i.getSiteType());
					updateSiteRequest.getSiteUpdationData().setStatus(i.getStatus());
					updateSiteRequest.getSiteUpdationData().setActive(i.isActive());
					updateSiteRequest.getSiteUpdationData().setTenantId(i.getTenantId());
					updateSiteRequest.getSiteUpdationData().setApplicationStartDate(i.getApplicationStartDate());
					updateSiteRequest.getSiteUpdationData().setApplicationEndDate(i.getApplicationEndDate());
					updateSiteRequest.getSiteUpdationData().setBookingPeriodStartDate(i.getBookingPeriodStartDate());
					updateSiteRequest.getSiteUpdationData().setBookingPeriodEndDate(i.getBookingPeriodEndDate());
				});
			}

		} catch (Exception e) {
			throw new CustomException(e.getMessage(), "Data not Found to Update!!!");
		}

	}

	void updateSiteData(SiteUpdateRequest updateSiteRequest) {
		// siteRepository.updateSiteData(updateSiteRequest);
		producer.push(AdvtConstants.SITE_UPDATION, updateSiteRequest);
	}

	private void enrichUpdatedSite(SiteUpdateRequest updateSiteRequest) {
		AuditDetails auditDetails = null;
		if (null != updateSiteRequest.getRequestInfo().getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.lastModifiedBy(updateSiteRequest.getRequestInfo().getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
		}
		updateSiteRequest.getSiteUpdationData().setAuditDetails(auditDetails);
		updateSiteRequest.getSiteUpdationData()
				.setAccountId(updateSiteRequest.getRequestInfo().getUserInfo().getUuid());

	}

	public SiteSearchResponse search(SiteSearchRequest searchSiteRequest) {
		SiteSearchResponse siteSearchResponse = new SiteSearchResponse();
		List<SiteCreationData> siteSearchData = new ArrayList<>();
		try {

			if (null != searchSiteRequest.getRequestInfo()
					&& searchSiteRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("CITIZEN")) {
				searchSiteRequest.getSiteSearchData().setStatus("Available");
				searchSiteRequest.getSiteSearchData().setWorkflowStatus(Collections.singletonList("APPROVED"));
				searchSiteRequest.getSiteSearchData().setActive(true);
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			} else if (null != searchSiteRequest.getRequestInfo()
					&& searchSiteRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("EMPLOYEE")) {

				List<String> wfStatus = getAccountStatusListByRoles(
						searchSiteRequest.getRequestInfo().getUserInfo().getRoles().stream()
						.filter(r -> StringUtils.equalsAnyIgnoreCase(r.getCode(), "SITE_CREATOR", "SITE_APPROVER"))
						.findFirst().get().getTenantId(),
						searchSiteRequest.getRequestInfo().getUserInfo().getRoles());
				searchSiteRequest.getSiteSearchData().setWorkflowStatus(wfStatus);
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			} else {
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			}
			/* siteSearchData = siteRepository.searchSites(searchSiteRequest); */
			if (!CollectionUtils.isEmpty(siteSearchData)) {
				siteSearchResponse = getSearchResponseFromAccounts(siteSearchData);
			}
			if (CollectionUtils.isEmpty(siteSearchResponse.getSiteCreationData())) {
				siteSearchResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(searchSiteRequest.getRequestInfo(), false));
			} else {
				siteSearchResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(searchSiteRequest.getRequestInfo(), true));
			}
			processSiteResponse(siteSearchResponse);
			/*
			 * if (!CollectionUtils.isEmpty(siteSearchResponse.getSiteCreationData())) {
			 * siteSearchResponse.setResponseInfo(responseInfoFactory
			 * .createResponseInfoFromRequestInfo(searchSiteRequest.getRequestInfo(),
			 * true)); }
			 */
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return siteSearchResponse;
	}

	private void processSiteResponse(SiteSearchResponse siteSearchResponse) {
		try {
			if (!CollectionUtils.isEmpty(siteSearchResponse.getSiteCreationData())) {
				siteSearchResponse.setCount(siteSearchResponse.getSiteCreationData().size());
				siteSearchResponse
						.setInitiate((int) siteSearchResponse.getSiteCreationData().stream()
								.filter(license -> StringUtils.equalsIgnoreCase(
										SiteConstants.APPLICATION_STATUS_INITIATED, license.getWorkFlowStatus()))
								.count());
				siteSearchResponse.setPendingForModification((int) siteSearchResponse.getSiteCreationData().stream()
						.filter(license -> StringUtils.equalsIgnoreCase(
								SiteConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION, license.getWorkFlowStatus()))
						.count());
				siteSearchResponse
						.setReject((int) siteSearchResponse.getSiteCreationData().stream()
								.filter(license -> StringUtils.equalsIgnoreCase(
										SiteConstants.APPLICATION_STATUS_REJECTED, license.getWorkFlowStatus()))
								.count());
				siteSearchResponse
						.setApprove((int) siteSearchResponse.getSiteCreationData().stream()
								.filter(license -> StringUtils.equalsIgnoreCase(
										SiteConstants.APPLICATION_STATUS_APPROVED, license.getWorkFlowStatus()))
								.count());
				siteSearchResponse.setReturnToInitiator((int) siteSearchResponse.getSiteCreationData().stream()
						.filter(license -> StringUtils.equalsIgnoreCase(
								SiteConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION, license.getWorkFlowStatus()))
						.count());
			}

		} catch (Exception e) {
			throw new CustomException(e.getMessage(), "Count not found for the logged in User!!!");
		}

	}

	private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {
		List<String> rolesWithinTenant = advtConstants.getRolesByTenantId(tenantId, roles);
		Set<String> statusWithRoles = new HashSet();
		rolesWithinTenant.stream().forEach(role -> {

			if (StringUtils.equalsIgnoreCase(role, SiteConstants.USER_ROLE_SITE_CREATOR)) {
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_INITIATED);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_APPROVED);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_REJECTED);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
			}
			
			if (StringUtils.equalsIgnoreCase(role, SiteConstants.USER_ROLE_SITE_APPROVER)) {
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_APPROVED);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_REJECTED);
				statusWithRoles.add(SiteConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
			}

		});
		return new ArrayList<>(statusWithRoles);
	}


	private SiteSearchResponse getSearchResponseFromAccounts(List<SiteCreationData> siteSearchData) {
		SiteSearchResponse responseFromDB = SiteSearchResponse.builder().siteCreationData(siteSearchData).build();
		return responseFromDB;
	}

	public SiteCountResponse totalCount(SiteCountRequest siteCountRequest) {
		SiteCountResponse countResponse = new SiteCountResponse();
		String ulbName = null;
		/* try { */
		if (null != siteCountRequest.getTenantId() && !siteCountRequest.getTenantId().isEmpty()) {
			String[] ulb = siteCountRequest.getTenantId().split("\\.");
			ulbName = ulb[1];
			int siteCount = siteRepository.siteCount(ulbName);
			int statusCountAvailable = siteRepository.siteAvailableCount(ulbName);
			int statusCountBooked = siteRepository.siteBookedCount(ulbName);
			countResponse = SiteCountResponse.builder()
					.responseinfo(responseInfoFactory
							.createResponseInfoFromRequestInfo(siteCountRequest.getRequestInfo(), false))
					.siteCountData(SiteCountData.builder().siteCount(siteCount).siteBookedCount(statusCountBooked)
							.siteAvailableCount(statusCountAvailable).build())
					.build();
			if (null != countResponse) {
				countResponse.setResponseinfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(siteCountRequest.getRequestInfo(), true));
			}
		} else {
			throw new RuntimeException("Please provide valid Tenant Id!!!");
		}

		/*
		 * } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
		 */
		return countResponse;
	}

	public SiteCreationActionResponse siteActionStatus(SiteCreationActionRequest siteCreationActionRequest) {
		if (CollectionUtils.isEmpty(siteCreationActionRequest.getSiteId())) {
			throw new CustomException("INVALID REQUEST", "SiteId must not be Empty!!!");
		}
		Map<String, List<String>> siteActionMaps = new HashMap<>();
		SiteSearchCriteria criteria = SiteSearchCriteria.builder().siteId(siteCreationActionRequest.getSiteId())
				.build();
		List<SiteCreationData> sites = siteRepository.fetchSites(criteria);
		if (CollectionUtils.isEmpty(sites)) {
			throw new CustomException("SITE_DATA_NOT_FOUND", "No Site Data found with given Site Ids.");
		}
		Map<String, SiteCreationData> mapSites = sites.stream()
				.collect(Collectors.toMap(acc -> acc.getSiteID(), acc -> acc));
		String siteTenantId = sites.get(0).getTenantId();
		String siteBusinessId = AdvtConstants.BUSINESS_SERVICE_SITE_CREATION;

		// fetch business service search
		BusinessServiceResponse businessServiceResponse = workflowService
				.businessServiceSearchForSites(siteCreationActionRequest, siteTenantId, siteBusinessId);

		if (null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
			throw new CustomException("NO_BUSINESS_SERVICE_FOUND",
					"Business service not found for Site Ids: " + siteCreationActionRequest.getSiteId().toString());
		}

		List<String> rolesWithinTenant = getRolesWithinTenant(siteTenantId,
				siteCreationActionRequest.getRequestInfo().getUserInfo().getRoles());

		siteCreationActionRequest.getSiteId().stream().forEach(siteId -> {

			String status = mapSites.get(siteId).getStatus();
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), status) && !StringUtils
							.equalsAnyIgnoreCase(state.getApplicationStatus(), AdvtConstants.STATUS_APPROVED))
					.collect(Collectors.toList());

			// filtering actions based on roles
			List<String> actions = new ArrayList<>();
			stateList.stream().forEach(state -> {
				state.getActions().stream()
						.filter(action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
						.forEach(action -> {
							actions.add(action.getAction());
						});
			});

			siteActionMaps.put(siteId, actions);
		});

		List<SiteCreationDetail> siteDetailList = new ArrayList<>();
		siteActionMaps.entrySet().stream().forEach(entry -> {
			siteDetailList.add(SiteCreationDetail.builder().siteId(entry.getKey()).action(entry.getValue()).build());
		});

		SiteCreationActionResponse siteCreationResponse = SiteCreationActionResponse.builder()
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(siteCreationActionRequest.getRequestInfo(), true))
				.siteCreationDetail(siteDetailList).build();
		return siteCreationResponse;
	}

	private List<String> getRolesWithinTenant(String tenantId, List<Role> roles) {
		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}
	
	public AllSiteCountResponse getAllcounts() {
		AllSiteCountResponse response = new AllSiteCountResponse();
        List<Map<String, Object>> statusList = null;
        statusList = siteRepository.getAllCounts();
        
        if (!CollectionUtils.isEmpty(statusList)) {
        	response.setCountsData(
		                statusList.stream()
		                        .filter(Objects::nonNull) // Ensure no null entries
		                        .filter(status -> StringUtils.isNotEmpty(status.toString())) // Validate non-empty entries
		                        .collect(Collectors.toList())); // Collect the filtered list
			  
			  if (statusList.get(0).containsKey("total_applications")) {
		            Object totalApplicationsObj = statusList.get(0).get("total_applications");
		            if (totalApplicationsObj instanceof Number) { // Ensure the value is a number
		            	response.setApplicationTotalCount(((Number) totalApplicationsObj).longValue());
		            } else {
		                throw new IllegalArgumentException("total_applications is not a valid number");
		            }
		        }
		}
        return response;
	}	

}
