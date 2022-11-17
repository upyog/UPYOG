package org.bel.birthdeath.crdeath.service;

import java.util.List;

import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.bel.birthdeath.crdeath.kafka.producer.CrDeathProducer;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrDeathService {

    private final CrDeathProducer producer;
    private final CrDeathConfiguration deathConfig;

    @Autowired
    CrDeathService(CrDeathProducer producer,CrDeathConfiguration deathConfig){
        this.producer = producer;
        this.deathConfig = deathConfig;
    }
    
    public List<CrDeathDtl> create(CrDeathDtlRequest request) {
        // validate request
       // validatorService.validateCreate(request);

        // enrich request
       // enrichmentService.enrichCreate(request);

        producer.push(deathConfig.getSaveDeathDetailsTopic(), request);

        return request.getDeathCertificateDtls();
    }

    
}
