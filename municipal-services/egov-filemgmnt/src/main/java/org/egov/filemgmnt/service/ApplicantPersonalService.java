package org.egov.filemgmnt.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.enrichment.ApplicantPersonalEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.ApplicantPersonalRepository;
import org.egov.filemgmnt.util.MdmsUtil;
import org.egov.filemgmnt.validators.ApplicantPersonalValidator;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicantPersonalService {

    private final ApplicantPersonalValidator validatorService;
    private final ApplicantPersonalEnrichment enrichmentService;
    private final ApplicantPersonalRepository repository;
    private final Producer producer;
    private final MdmsUtil mdmsUtil;
    private final FMConfiguration filemgmntConfig;

    @Autowired
    ApplicantPersonalService(ApplicantPersonalValidator validatorService, ApplicantPersonalEnrichment enrichmentService,
                             ApplicantPersonalRepository repository, Producer producer, MdmsUtil mdmsUtil,
                             FMConfiguration filemgmntConfig) {
        this.validatorService = validatorService;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.mdmsUtil = mdmsUtil;
        this.filemgmntConfig = filemgmntConfig;
    }

    public List<ApplicantPersonal> create(ApplicantPersonalRequest request) {
        String tenantId = request.getApplicantPersonals()
                                 .get(0)
                                 .getTenantId();

        // validate mdms data
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo(), tenantId);

        // validate request
        validatorService.validateCreate(request, mdmsData);

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(filemgmntConfig.getSaveApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();

    }

    public List<ApplicantPersonal> update(ApplicantPersonalRequest request) {

        String id = request.getApplicantPersonals()
                           .get(0)
                           .getId();

        // search database
        List<ApplicantPersonal> searchResult = repository.getApplicantPersonals(ApplicantPersonalSearchCriteria.builder()
                                                                                                               .id(id)
                                                                                                               .build());

        // validate request
        validatorService.validateUpdate(request, searchResult);

        enrichmentService.enrichUpdate(request);

        producer.push(filemgmntConfig.getUpdateApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();
    }

    public List<ApplicantPersonal> search(ApplicantPersonalSearchCriteria criteria, RequestInfo requestInfo) {
        validatorService.validateSearch(requestInfo, criteria);
        return repository.getApplicantPersonals(criteria);
    }
}
