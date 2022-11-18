package org.egov.filemgmnt.enrichment;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.config.CommunicationFileManagementConfiguration;
import org.egov.filemgmnt.util.IdgenUtil;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.egov.filemgmnt.web.models.CommunicationFileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommunicationFileManagementEnrichment implements BaseEnrichment {

	private final CommunicationFileManagementConfiguration config;
	private final IdgenUtil idgenUtil;

	@Autowired
	CommunicationFileManagementEnrichment(CommunicationFileManagementConfiguration config, IdgenUtil idgenUtil) {
		this.config = config;
		this.idgenUtil = idgenUtil;

	}

	public void enrichCreate(CommunicationFileRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		User userInfo = requestInfo.getUserInfo();

		AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

		request.getCommunicationFiles().forEach(communication -> {
			communication.setId(UUID.randomUUID().toString());
			communication.setAuditDetails(auditDetails);
		});

	}

	public void enrichUpdate(CommunicationFileRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		User userInfo = requestInfo.getUserInfo();

		AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

		request.getCommunicationFiles().forEach(communication -> communication.setAuditDetails(auditDetails));
	}

}
