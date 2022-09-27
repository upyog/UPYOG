package org.egov.filemgmnt.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.enrichment.ApplicantPersonalEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.ApplicantPersonalRepository;
import org.egov.filemgmnt.validators.ApplicantPersonalValidator;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicantPersonalService {

    private final Producer producer;
    private final FilemgmntConfiguration filemgmntConfig;
    private final ApplicantPersonalValidator validatorService;
    private final ApplicantPersonalEnrichment enrichmentService;
    private final ApplicantPersonalRepository repository;

    @Autowired
    ApplicantPersonalService(ApplicantPersonalValidator validatorService, ApplicantPersonalEnrichment enrichmentService,
                             ApplicantPersonalRepository repository, Producer producer,
                             FilemgmntConfiguration filemgmntConfig) {
        this.validatorService = validatorService;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.filemgmntConfig = filemgmntConfig;
    }

    public List<ApplicantPersonal> create(ApplicantPersonalRequest request) {
        // validate request
        validatorService.validateCreate(request);

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(filemgmntConfig.getSaveApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();
    }

    public List<ApplicantPersonal> search(ApplicantPersonalSearchCriteria criteria) {

        if (CollectionUtils.isEmpty(criteria.getIds())) {
//            throw new GlobalException(ErrorCodes.APPLICANT_PERSONAL_INVALID_SEARCH_CRITERIA,
//                    "At least one applicant id is required.");
        }

        return repository.getApplicantPersonals(criteria);
    }

    public List<ApplicantPersonal> update(ApplicantPersonalRequest request) {

        List<String> ids = new LinkedList<>();
        request.getApplicantPersonals()
               .forEach(personal -> ids.add(personal.getId()));

        // search database
        List<ApplicantPersonal> searchResult = repository.getApplicantPersonals(ApplicantPersonalSearchCriteria.builder()
                                                                                                               .ids(ids)
                                                                                                               .build());
        // validate request
        validatorService.validateUpdate(request, searchResult);

        enrichmentService.enrichUpdate(request);

        producer.push(filemgmntConfig.getSaveApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();
    }
}
