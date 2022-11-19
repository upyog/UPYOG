package org.egov.filemgmnt.validators;

import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.repository.CommunicationFileManagementRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommunicationFileManagementValidator {

    private final CommunicationFileManagementRepository commRepository;
    private final FMConfiguration config;
    private final MdmsValidator mdmsValidator;

    public CommunicationFileManagementValidator(CommunicationFileManagementRepository commRepository,
                                                FMConfiguration config, MdmsValidator mdmsValidator) {

        this.commRepository = commRepository;
        this.config = config;
        this.mdmsValidator = mdmsValidator;
    }

}
