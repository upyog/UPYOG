package org.ksmart.birth.ksmartbirthapplication.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.ksmartbirthapplication.enrichment.KsmartBirthEnrichment;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class KsmartBirthRepository {
    private final BndProducer producer;
    private final  KsmartBirthEnrichment ksmartBirthEnrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    KsmartBirthRepository(JdbcTemplate jdbcTemplate, KsmartBirthEnrichment ksmartBirthEnrichment,
                               BirthConfiguration birthDeathConfiguration, BndProducer producer) {
        this.jdbcTemplate = jdbcTemplate;
        this.ksmartBirthEnrichment = ksmartBirthEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
    }

    public List<KsmartBirthAppliactionDetail> saveKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        ksmartBirthEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveKsmartBirthApplicationTopic(), request);
        return request.getKsmartBirthDetails();
    }

}
