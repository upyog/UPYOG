package org.egov.filemgmnt.service;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.enrichment.ApplicantServiceEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.ApplicantServiceRepository;
import org.egov.filemgmnt.validators.ApplicantServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceDetailsService {

    private final Producer producer;
    private final FilemgmntConfiguration filemgmntConfig;
    private final ApplicantServiceValidator validatorService;
    private final ApplicantServiceEnrichment enrichmentService;
    private final ApplicantServiceRepository repository;

    @Autowired
    ApplicantServiceService(ApplicantServiceValidator validatorService, ApplicantServiceEnrichment enrichmentService,
                            ApplicantServiceRepository repository, Producer producer,
                            FilemgmntConfiguration filemgmntConfig) {
        this.validatorService = validatorService;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.filemgmntConfig = filemgmntConfig;

    }

    public List<ApplicantService> create(@Valid ApplicantServiceRequest request) {
        // validate request
        validatorService.validateCreate(request);

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(filemgmntConfig.getSaveApplicantServiceTopic(), request);

        return request.getApplicantServices();
    }

    public List<ApplicantService> search(ApplicantServiceSearchCriteria criteria) {

        if (CollectionUtils.isEmpty(criteria.getIds())) {
//            throw new GlobalException(ErrorCodes.APPLICANT_SERVICE_INVALID_SEARCH_CRITERIA,
//                    "At least one applicant id is required.");
        }

        return repository.getApplicantServices(criteria);
    }

    public List<ApplicantService> update(ApplicantServiceRequest request) {

        List<String> ids = new LinkedList<>();
        request.getApplicantServices()
               .forEach(personal -> ids.add(personal.getId()));

        // search database
        List<ApplicantService> searchResult = repository.getApplicantServices(ApplicantServiceSearchCriteria.builder()
                                                                                                            .ids(ids)
                                                                                                            .build());
        // validate request
        validatorService.validateUpdate(request, searchResult);

        enrichmentService.enrichUpdate(request);

        producer.push(filemgmntConfig.getSaveApplicantPersonalTopic(), request);

        return request.getApplicantServices();
    }

}
