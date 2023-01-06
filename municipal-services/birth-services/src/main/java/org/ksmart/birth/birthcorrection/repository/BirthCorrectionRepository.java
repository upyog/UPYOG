package org.ksmart.birth.birthcorrection.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcorrection.enrichment.birth.BirthDetailsCorrectionEnrichment;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.repository.querybuilder.BirthApplicationQueryBuilder;
import org.ksmart.birth.birthapplication.repository.rowmapper.BirthApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class BirthCorrectionRepository {

    private final BndProducer producer;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final BirthApplicationQueryBuilder birthQueryBuilder;
    private final BirthApplicationRowMapper birthApplicationRowMapper;
    private final BirthDetailsCorrectionEnrichment birthDetailsCorrectionEnrichment;

    @Autowired
    BirthCorrectionRepository(JdbcTemplate jdbcTemplate, BirthApplicationQueryBuilder birthQueryBuilder,
                              BirthApplicationRowMapper birthApplicationRowMapper, BirthDetailsCorrectionEnrichment birthDetailsCorrectionEnrichment,
                              BirthConfiguration birthDeathConfiguration, BndProducer producer) {
        this.jdbcTemplate = jdbcTemplate;
        this.birthQueryBuilder = birthQueryBuilder;
        this.birthApplicationRowMapper = birthApplicationRowMapper;
        this.birthDetailsCorrectionEnrichment = birthDetailsCorrectionEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
    }

    public List<BirthApplicationDetail> saveBirthDetails(BirthDetailsRequest request) {
        birthDetailsCorrectionEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBirthApplicationTopic(), request);
        return request.getBirthDetails();
    }


    public List<BirthApplicationDetail> updateBirthDetails(BirthDetailsRequest request) {
        birthDetailsCorrectionEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBirthApplicationTopic(), request);
        return request.getBirthDetails();
    }

    public List<BirthApplicationDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = birthQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        List<BirthApplicationDetail> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), birthApplicationRowMapper);
        return result;
    }
}
