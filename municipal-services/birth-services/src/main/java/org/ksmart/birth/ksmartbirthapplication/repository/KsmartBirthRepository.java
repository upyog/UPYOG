package org.ksmart.birth.ksmartbirthapplication.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.ksmartbirthapplication.enrichment.KsmartBirthEnrichment;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationSearchCriteria;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.repository.querybuilder.KsmartBirthApplicationQueryBuilder;
import org.ksmart.birth.ksmartbirthapplication.repository.querybuilder.KsmartQueryBuilder;
import org.ksmart.birth.ksmartbirthapplication.repository.rowmapper.KsmartBirthApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class KsmartBirthRepository {
    private final BndProducer producer;
    private final  KsmartBirthEnrichment ksmartBirthEnrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final KsmartBirthApplicationQueryBuilder birthQueryBuilder;

    private final KsmartBirthApplicationRowMapper ksmartBirthApplicationRowMapper;


    @Autowired
    KsmartBirthRepository(JdbcTemplate jdbcTemplate, KsmartBirthEnrichment ksmartBirthEnrichment, BirthConfiguration birthDeathConfiguration,
                          BndProducer producer, KsmartBirthApplicationQueryBuilder birthQueryBuilder, KsmartBirthApplicationRowMapper ksmartBirthApplicationRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ksmartBirthEnrichment = ksmartBirthEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.birthQueryBuilder = birthQueryBuilder;


        this.ksmartBirthApplicationRowMapper = ksmartBirthApplicationRowMapper;
    }

    public List<KsmartBirthAppliactionDetail> saveKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        ksmartBirthEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveKsmartBirthApplicationTopic(), request);
        return request.getKsmartBirthDetails();
    }

    public List<KsmartBirthAppliactionDetail> updateKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        ksmartBirthEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateKsmartBirthApplicationTopic(), request);
        return request.getKsmartBirthDetails();
    }


    public List<KsmartBirthAppliactionDetail> searchKsmartBirthDetails(@Valid KsmartBirthApplicationSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = birthQueryBuilder.getKsmartBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        List<KsmartBirthAppliactionDetail> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), ksmartBirthApplicationRowMapper);

        return result;
    }
}
