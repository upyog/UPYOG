package org.egov.advertisementcanopy.service;

import java.util.Date;
import java.util.UUID;

import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteCreationRequest;
import org.egov.advertisementcanopy.model.SiteCreationResponse;
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
		// String ids= createSiteRequest.getCreationData().getSiteID();
		// SiteCreationData siteIds=
		// siteRepository.searchSiteIds(SearchCriteriaSite.builder().siteId(Collections.singletonList(ids)).build());
		// validateSiteDataDuplicacy(createSiteRequest);
		SiteCreationData siteCreationData = new SiteCreationData();

		// enrich Site
		enrichSiteWhileCreation(createSiteRequest);

		createSiteObjects(createSiteRequest);
		SiteCreationResponse siteCreationResponse = SiteCreationResponse
				.builder().responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(createSiteRequest.getRequestInfo(), false))
				.creationData(createSiteRequest.getCreationData()).build();
		if (null != siteCreationResponse) {
			siteCreationResponse.setResponseInfo(
					responseInfoFactory.createResponseInfoFromRequestInfo(createSiteRequest.getRequestInfo(), true));
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
			createSiteRequest.getCreationData().setAccountId(createSiteRequest.getRequestInfo().getUserInfo().getUuid());
			createSiteRequest.getCreationData().setUuid(UUID.randomUUID().toString());
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private void createSiteObjects(SiteCreationRequest createSiteRequest) {
		siteRepository.create(createSiteRequest.getCreationData());

	}

	private void validateSiteDataDuplicacy(SiteCreationRequest createSiteRequest) {
		// TODO Auto-generated method stub

	}

}
