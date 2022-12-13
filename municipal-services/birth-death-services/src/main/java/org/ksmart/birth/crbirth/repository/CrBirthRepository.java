package org.ksmart.birth.crbirth.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthDeathConfiguration;
import org.ksmart.birth.crbirth.enrichment.birth.BirthDetailsEnrichment;
import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.ksmart.birth.crbirth.model.BirthDetail;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.crbirth.repository.querybuilder.BirthApplicationQueryBuilder;
import org.ksmart.birth.crbirth.repository.rowmapper.BirthApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CrBirthRepository {

    private final BndProducer producer;
    private final BirthDeathConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final BirthApplicationQueryBuilder birthQueryBuilder;
    private final BirthApplicationRowMapper birthApplicationRowMapper;
    private final BirthDetailsEnrichment birthDetailsEnrichment;

    @Autowired
    CrBirthRepository(JdbcTemplate jdbcTemplate, BirthApplicationQueryBuilder birthQueryBuilder,
                      BirthApplicationRowMapper birthApplicationRowMapper, BirthDetailsEnrichment birthDetailsEnrichment,
                      BirthDeathConfiguration birthDeathConfiguration, BndProducer producer) {
        this.jdbcTemplate = jdbcTemplate;
        this.birthQueryBuilder = birthQueryBuilder;
        this.birthApplicationRowMapper = birthApplicationRowMapper;
        this.birthDetailsEnrichment = birthDetailsEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
    }

    public List<BirthDetail> saveBirthDetails(BirthDetailsRequest request) {
        birthDetailsEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBirthApplicationTopic(), request);
        return request.getBirthDetails();
    }


    public List<BirthDetail> updateBirthDetails(BirthDetailsRequest request) {
        birthDetailsEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBirthApplicationTopic(), request);
        return request.getBirthDetails();
    }

    public List<BirthDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = birthQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        List<BirthDetail> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), birthApplicationRowMapper);
        return result;
    }
}
