package org.egov.filemgmnt.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
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
    private final FilemgmntConfiguration fmConfig;

    @Autowired
    ApplicantPersonalService(ApplicantPersonalValidator validatorService, ApplicantPersonalEnrichment enrichmentService,
                             ApplicantPersonalRepository repository, Producer producer, MdmsUtil mdmsUtil,
                             FilemgmntConfiguration filemgmntConfig) {
        this.validatorService = validatorService;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.producer = producer;
        this.mdmsUtil = mdmsUtil;
        this.fmConfig = filemgmntConfig;
    }

    public List<ApplicantPersonal> create(ApplicantPersonalRequest request) {

        // validate mdms data
        String tenantId = request.getApplicantPersonals()
                                 .get(0)
                                 .getTenantId();
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo(), tenantId);

        // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(fmConfig.getSaveApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();

    }

    public List<ApplicantPersonal> search(ApplicantPersonalSearchCriteria criteria, RequestInfo requestInfo) {

        List<ApplicantPersonal> result = null;

        validatorService.validateSearch(requestInfo, criteria);
        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            result = repository.getApplicantPersonals(criteria);
        } else if (!CollectionUtils.isEmpty(criteria.getFileCodes())) {
            result = repository.getApplicantPersonalsFromFilecode(criteria);
        } else if (criteria.getFromDate() != null) {
            result = repository.getApplicantPersonalsFromDate(criteria);
        } else if (!StringUtils.isEmpty(criteria.getAadhaarno())) {
            result = repository.getApplicantPersonalsFromDate(criteria);
        }

        return result;
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

        producer.push(fmConfig.getUpdateApplicantPersonalTopic(), request);

        return request.getApplicantPersonals();
    }
}
