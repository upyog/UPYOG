package org.egov.filemgmnt.service;

import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.enrichment.ApplicantPersonalEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicantPersonalService {

    private ApplicantPersonalEnrichment enrichmentService;
    private FilemgmntConfiguration filemgmntConfig;
    private Producer producer;

    @Autowired
    ApplicantPersonalService(ApplicantPersonalEnrichment enrichmentService,
            Producer producer,
            FilemgmntConfiguration filemgmntConfig) {
        this.enrichmentService = enrichmentService;
        this.producer = producer;
        this.filemgmntConfig = filemgmntConfig;
    }

    public void create(ApplicantPersonalRequest request) {
        request.getPersonals()//
               .forEach(enrichmentService::enrichApplicantPersonal);

        producer.push(filemgmntConfig.getSaveApplicantPersonalTopic(), request);
    }
}
