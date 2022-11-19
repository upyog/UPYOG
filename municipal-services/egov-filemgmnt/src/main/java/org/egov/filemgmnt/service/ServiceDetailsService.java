package org.egov.filemgmnt.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.enrichment.ServiceDetailsEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.ServiceDetailsRepository;
import org.egov.filemgmnt.validators.ServiceDetailsValidator;
import org.egov.filemgmnt.web.models.ServiceDetails;
import org.egov.filemgmnt.web.models.ServiceDetailsRequest;
import org.egov.filemgmnt.web.models.ServiceDetailsSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceDetailsService {

    private final ServiceDetailsValidator validatorService;
    private final ServiceDetailsEnrichment enrichmentService;
    private final ServiceDetailsRepository repository;
    private final Producer producer;
    private final FMConfiguration fmConfig;

    @Autowired
    ServiceDetailsService(ServiceDetailsValidator validatorService, ServiceDetailsEnrichment enrichmentService,
                          ServiceDetailsRepository repository, Producer producer, FMConfiguration fmConfig) {
        this.validatorService = validatorService;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.fmConfig = fmConfig;

    }

    public List<ServiceDetails> create(ServiceDetailsRequest request) {
        // validate request
        validatorService.validateCreate(request);

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(fmConfig.getSaveServiceDetailsTopic(), request);

        return request.getServiceDetails();
    }

    public List<ServiceDetails> search(ServiceDetailsSearchCriteria criteria) {

        if (CollectionUtils.isEmpty(criteria.getIds())) {
//            throw new GlobalException(ErrorCodes.APPLICANT_SERVICE_INVALID_SEARCH_CRITERIA,
//                    "At least one applicant id is required.");
        }

        return repository.getApplicantServices(criteria);
    }

    public List<ServiceDetails> update(ServiceDetailsRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("ServiceDetails.update\n{}", request);
        }

        List<String> ids = new LinkedList<>();
        request.getServiceDetails()
               .forEach(personal -> ids.add(personal.getId()));

        // search database
        List<ServiceDetails> searchResult = repository.getApplicantServices(ServiceDetailsSearchCriteria.builder()
                                                                                                        .ids(ids)
                                                                                                        .build());
        // validate request
        validatorService.validateUpdate(request, searchResult);

        enrichmentService.enrichUpdate(request);

        producer.push(fmConfig.getSaveApplicantPersonalTopic(), request);

        return request.getServiceDetails();
    }

}
