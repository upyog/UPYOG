package org.egov.advertisementcanopy.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteCreationRequest;
import org.egov.advertisementcanopy.model.SiteCreationResponse;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.model.SiteUpdationResponse;
import org.egov.advertisementcanopy.repository.SiteRepository;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiteService {

	@Autowired
	SiteRepository siteRepository;

	@Autowired
	ResponseInfoFactory responseInfoFactory;

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
			if (null != ids && !ids.isEmpty()) {
				throw new RuntimeException("Site already exists...Duplicate Site!!!");
			}
			// enrich Site
			enrichSiteWhileCreation(createSiteRequest);
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
			createSiteRequest.getCreationData()
					.setAccountId(createSiteRequest.getRequestInfo().getUserInfo().getUuid());
			createSiteRequest.getCreationData().setUuid(UUID.randomUUID().toString());

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private void createSiteObjects(SiteCreationRequest createSiteRequest) {
		siteRepository.create(createSiteRequest.getCreationData());

	}

	public SiteUpdationResponse update(SiteUpdateRequest updateSiteRequest) {
		SiteUpdationResponse siteupdationResponse = null;
		List<SiteCreationData> list = new ArrayList<>();
//		try {
			if (null != updateSiteRequest.getSiteUpdationData()) {
				list=siteRepository.searchSites(updateSiteRequest.getSiteUpdationData());
			}
			enrichUpdatedSite(updateSiteRequest);
			updateSiteData(updateSiteRequest);

			siteupdationResponse = SiteUpdationResponse.builder()
					.responseInfo(responseInfoFactory
							.createResponseInfoFromRequestInfo(updateSiteRequest.getRequestInfo(), false))
					.siteCreationData(updateSiteRequest.getSiteUpdationData()).build();
			if (null != siteupdationResponse) {
				siteupdationResponse.setResponseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(updateSiteRequest.getRequestInfo(), true));
			}

//		} catch (Exception e) {
//			throw new RuntimeException("Details provided for Site Updation are invalid!!!");
//		}

		return siteupdationResponse;
	}

	private void updateSiteData(SiteUpdateRequest updateSiteRequest) {
		siteRepository.updateSiteData(updateSiteRequest.getSiteUpdationData());

	}

	private void enrichUpdatedSite(SiteUpdateRequest updateSiteRequest) {
		AuditDetails auditDetails = null;
		if (null != updateSiteRequest.getRequestInfo().getUserInfo()) {
			auditDetails = AuditDetails.builder().createdBy(updateSiteRequest.getRequestInfo().getUserInfo().getUuid())
					.createdDate(new Date().getTime())
					.lastModifiedBy(updateSiteRequest.getRequestInfo().getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
		}
		updateSiteRequest.getSiteUpdationData().setAuditDetails(auditDetails);

	}

}
