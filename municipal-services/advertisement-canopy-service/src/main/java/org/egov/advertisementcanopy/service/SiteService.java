package org.egov.advertisementcanopy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteCountData;
import org.egov.advertisementcanopy.model.SiteCountRequest;
import org.egov.advertisementcanopy.model.SiteCountResponse;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteCreationRequest;
import org.egov.advertisementcanopy.model.SiteCreationResponse;
import org.egov.advertisementcanopy.model.SiteSearchRequest;
import org.egov.advertisementcanopy.model.SiteSearchResponse;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.model.SiteUpdationResponse;
import org.egov.advertisementcanopy.producer.Producer;
import org.egov.advertisementcanopy.repository.SiteRepository;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.egov.advertisementcanopy.util.PTRConstants;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.egov.common.contract.request.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
			// Create Sites
			createSiteObjects(createSiteRequest);
			workflowService.createWorkflowStatus(createSiteRequest);
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
		try {
			if (null != updateSiteRequest.getSiteUpdationData()) {
				list = siteRepository.searchSites(updateSiteRequest.getSiteUpdationData());
			}
			if (!CollectionUtils.isEmpty(list)) {
				enrichUpdatedSite(updateSiteRequest);
				updateSiteData(updateSiteRequest);
			} else {
				throw new RuntimeException("No Site exists with the Details Provided!!!");
			}
			workflowService.createWorkflowStatusForUpdate(updateSiteRequest);
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

	private void updateSiteData(SiteUpdateRequest updateSiteRequest) {
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
				searchSiteRequest.getSiteSearchData().setWorkflowStatus(Collections.singletonList("Approved"));
				searchSiteRequest.getSiteSearchData().setActive(true);
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			} else if (null != searchSiteRequest.getRequestInfo()
					&& searchSiteRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("EMPLOYEE")) {

				List<String> wfStatus = getAccountStatusListByRoles(
						searchSiteRequest.getRequestInfo().getUserInfo().getUuid(),
						searchSiteRequest.getRequestInfo().getUserInfo().getRoles());
				searchSiteRequest.getSiteSearchData().setWorkflowStatus(wfStatus);
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			}
			else {
				siteSearchData = siteRepository.searchSites(searchSiteRequest);
			}
			siteSearchData = siteRepository.searchSites(searchSiteRequest);
			if (!CollectionUtils.isEmpty(siteSearchData)) {
				siteSearchResponse = getSearchResponseFromAccounts(siteSearchData);
			}
			if (CollectionUtils.isEmpty(siteSearchResponse.getSiteCreationData())) {
				siteSearchResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(searchSiteRequest.getRequestInfo(), false));
			}
			if (!CollectionUtils.isEmpty(siteSearchResponse.getSiteCreationData())) {
				siteSearchResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(searchSiteRequest.getRequestInfo(), true));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return siteSearchResponse;
	}

	private List<String> getAccountStatusListByRoles(String uuid, List<Role> roles) {
		List<String> rolesWithinTenant = getRolesByTenantId(uuid, roles);
		Set<String> statusWithRoles = new HashSet();
		rolesWithinTenant.stream().forEach(role -> {

			if (StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_SITE_VERIFIER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_INITIATED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_APPROVED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_REJECTED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
			} else if (StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_SITE_APPROVER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_APPROVED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_REJECTED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
			}

		});
		return new ArrayList<>(statusWithRoles);
	}

	private List<String> getRolesByTenantId(String uuid, List<Role> roles) {
		List<String> roleCodes = roles.stream().filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), uuid))
				.map(role -> role.getCode()).collect(Collectors.toList());
		return roleCodes;
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

}
