package org.egov.filemgmnt.service;

import java.util.List;

import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.enrichment.CommunicationFileManagementEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.CommunicationFileManagementRepository;
import org.egov.filemgmnt.util.MdmsUtil;
import org.egov.filemgmnt.validators.CommunicationFileManagementValidator;
import org.egov.filemgmnt.web.models.CommunicationFile;
import org.egov.filemgmnt.web.models.CommunicationFileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunicationFileManagementService {

    private final CommunicationFileManagementValidator validator;
    private final CommunicationFileManagementEnrichment enrichmentService;
    private final CommunicationFileManagementRepository repository;
    private final Producer producer;
    private final MdmsUtil mdmsUtil;
    private final FMConfiguration fmConfig;

    @Autowired
    CommunicationFileManagementService(CommunicationFileManagementValidator validator,
                                       CommunicationFileManagementEnrichment enrichmentService,
                                       CommunicationFileManagementRepository repository, Producer producer,
                                       MdmsUtil mdmsUtil, FMConfiguration fmConfig) {

        this.validator = validator;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.mdmsUtil = mdmsUtil;
        this.fmConfig = fmConfig;
    }

    public List<CommunicationFile> create(CommunicationFileRequest request) {

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(fmConfig.getSaveCommunicationFileTopic(), request);

        return request.getCommunicationFiles();

    }

}
