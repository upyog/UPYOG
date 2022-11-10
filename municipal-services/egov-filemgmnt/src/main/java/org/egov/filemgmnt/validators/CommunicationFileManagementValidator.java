package org.egov.filemgmnt.validators;

import org.egov.filemgmnt.config.CommunicationFileManagementConfiguration;
import org.egov.filemgmnt.repository.CommunicationFileManagementRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommunicationFileManagementValidator {

	private final CommunicationFileManagementRepository commRepository;
	private final CommunicationFileManagementConfiguration config;
	private final MdmsValidator mdmsValidator;

	public CommunicationFileManagementValidator(CommunicationFileManagementRepository commRepository,
			CommunicationFileManagementConfiguration config, MdmsValidator mdmsValidator) {

		this.commRepository = commRepository;
		this.config = config;
		this.mdmsValidator = mdmsValidator;
	}

}
