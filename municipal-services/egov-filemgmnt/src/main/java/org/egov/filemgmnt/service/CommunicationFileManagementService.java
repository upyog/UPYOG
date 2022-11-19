package org.egov.filemgmnt.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.enrichment.CommunicationFileManagementEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.CommunicationFileManagementRepository;
import org.egov.filemgmnt.util.MdmsUtil;
import org.egov.filemgmnt.validators.CommunicationFileManagementValidator;
import org.egov.filemgmnt.web.models.CommunicationFile;
import org.egov.filemgmnt.web.models.CommunicationFileRequest;
import org.egov.filemgmnt.web.models.CommunicationFileSearchCriteria;
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

    public List<CommunicationFile> update(CommunicationFileRequest request) {

        List<String> ids = new LinkedList<>();

        request.getCommunicationFiles()
               .forEach(file -> ids.add(file.getId()));

        // search database
        List<CommunicationFile> searchResult = repository.getCommunicationfiles(CommunicationFileSearchCriteria.builder()
                                                                                                               .ids(ids)
                                                                                                               .build());

        enrichmentService.enrichUpdate(request);

        producer.push(fmConfig.getUpdateCommunicationFileTopic(), request);

        return request.getCommunicationFiles();
    }

    public List<CommunicationFile> search(CommunicationFileSearchCriteria criteria, RequestInfo requestInfo) {

        List<CommunicationFile> result = null;
        validator.validateSearch(requestInfo, criteria);
        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            result = repository.getCommunicationfiles(criteria);
        }
        return result;
    }

}
