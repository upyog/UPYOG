package org.ksmart.birth.birthregistry.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.enrichment.RegisterBirthDetailsEnrichment;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.repository.querybuilder.RegisterQueryBuilder;
import org.ksmart.birth.birthregistry.repository.rowmapper.BirthRegisterRowMapper;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthDeathConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Repository
public class RegisterBirthRepository {
    private final BndProducer producer;
    private final BirthDeathConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final RegisterBirthDetailsEnrichment registerBirthDetailsEnrichment;
    private final BirthRegisterRowMapper birthRegisterRowMapper;
    private final RegisterQueryBuilder registerQueryBuilder;

    @Autowired
    RegisterBirthRepository(JdbcTemplate jdbcTemplate, RegisterBirthDetailsEnrichment registerBirthDetailsEnrichment,
                      BirthDeathConfiguration birthDeathConfiguration, BndProducer producer, RegisterQueryBuilder registerQueryBuilder,
                      BirthRegisterRowMapper birthRegisterRowMapper ) {
        this.jdbcTemplate = jdbcTemplate;
        this.registerBirthDetailsEnrichment = registerBirthDetailsEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.registerQueryBuilder = registerQueryBuilder;
        this.birthRegisterRowMapper = birthRegisterRowMapper;
    }
    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        registerBirthDetailsEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBirthRegisterTopic(), request);
        return request.getRegisterBirthDetails();
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        registerBirthDetailsEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBirthRegisterTopic(), request);
        return request.getRegisterBirthDetails();
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = registerQueryBuilder.getRegBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        List<RegisterBirthDetail> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), birthRegisterRowMapper);
        return result;
    }
}
