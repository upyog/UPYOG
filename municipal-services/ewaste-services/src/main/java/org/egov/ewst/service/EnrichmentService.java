package org.egov.ewst.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

@Service
public class EnrichmentService {

	@Autowired
	private EwasteConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private EwasteUtil ewasteUtil;

	public void enrichEwasteApplication(EwasteRegistrationRequest ewasteRegistrationRequest) {

		List<String> customIds = ewasteUtil.getIdList(ewasteRegistrationRequest.getRequestInfo(),
				ewasteRegistrationRequest.getEwasteApplication().get(0).getTenantId(), config.getEwasteIdGenName(),
				config.getEwasteIdGenFormat(), 1);

		EwasteApplication eApplication = ewasteRegistrationRequest.getEwasteApplication().get(0);
		AuditDetails auditDetails = AuditDetails.builder()
				.createdBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid())
				.createdTime(System.currentTimeMillis())
				.lastModifiedBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid())
				.lastModifiedTime(System.currentTimeMillis()).build();
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

	public void enrichEwasteApplicationUponUpdate(EwasteRegistrationRequest ewasteRegistrationRequest) {
		// TODO Auto-generated method stub
		ewasteRegistrationRequest.getEwasteApplication().get(0).getAuditDetails()
				.setLastModifiedTime(System.currentTimeMillis());
		ewasteRegistrationRequest.getEwasteApplication().get(0).getAuditDetails()
				.setLastModifiedBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
	}

}
