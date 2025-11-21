package org.egov.ewst.service;

import java.util.List;
import java.util.UUID;
import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.models.AuditDetails;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteDetails;
import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.models.enums.Status;
import org.egov.ewst.util.EwasteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service class responsible for enriching Ewaste application data.
 * This class contains methods to enrich the Ewaste application with necessary details
 * such as unique IDs, audit details, and request status.
 */
@Service
public class EnrichmentService {

	@Autowired
	private EwasteConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private EwasteUtil ewasteUtil;

	/**
	 * Enriches the Ewaste application with necessary details.
	 * This method sets unique IDs, audit details, and request status for the Ewaste application.
	 *
	 * @param ewasteRegistrationRequest The Ewaste registration request containing the application details.
	 */
	public void enrichEwasteApplication(EwasteRegistrationRequest ewasteRegistrationRequest) {

		List<String> customIds = ewasteUtil.getIdList(ewasteRegistrationRequest.getRequestInfo(),
				ewasteRegistrationRequest.getEwasteApplication().get(0).getTenantId(), config.getEwasteIdGenName(),
				config.getEwasteIdGenFormat(), 1);

		EwasteApplication eApplication = ewasteRegistrationRequest.getEwasteApplication().get(0);
		AuditDetails auditDetails = AuditDetails.builder()
				.createdBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid())
				.createdTime(System.currentTimeMillis()).lastModifiedBy(null).lastModifiedTime(null).build();
		eApplication.setAuditDetails(auditDetails);
		eApplication.setId(UUID.randomUUID().toString());
		eApplication.getAddress().setRegistrationId(eApplication.getId());
		eApplication.getAddress().setId(UUID.randomUUID().toString());
		eApplication.getAddress().setAuditDetails(auditDetails);
		eApplication.getApplicant().setEwId(eApplication.getId());
		eApplication.getApplicant().setId(UUID.randomUUID().toString());
		eApplication.setRequestId(customIds.get(0));
//		eApplication.setRequestId("ABC-1");
		eApplication.setRequestStatus(Status.NEWREQUEST.toString());
		// Enrich each ewasteDetails object
		for (EwasteDetails detail : eApplication.getEwasteDetails()) {
			detail.setId(UUID.randomUUID().toString()); // Set a unique ID for each ewasteDetail
		}
		if (!CollectionUtils.isEmpty(eApplication.getDocuments()))
			eApplication.getDocuments().forEach(doc -> {
				if (doc.getId() == null) {
					doc.setId(UUID.randomUUID().toString());
					doc.setActive(true);
					doc.setTenantId(eApplication.getTenantId());
					doc.setAuditDetails(eApplication.getAuditDetails());

				}
			});
	}

	/**
	 * Enriches the Ewaste application upon update.
	 * This method sets the last modified time and user for the Ewaste application.
	 *
	 * @param ewasteRegistrationRequest The Ewaste registration request containing the application details.
	 */
	public void enrichEwasteApplicationUponUpdate(EwasteRegistrationRequest ewasteRegistrationRequest) {
		// TODO Auto-generated method stub
		ewasteRegistrationRequest.getEwasteApplication().get(0).getAuditDetails()
				.setLastModifiedTime(System.currentTimeMillis());
		ewasteRegistrationRequest.getEwasteApplication().get(0).getAuditDetails()
				.setLastModifiedBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
	}

}
