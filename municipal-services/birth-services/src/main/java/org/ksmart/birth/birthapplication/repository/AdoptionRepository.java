package org.ksmart.birth.birthapplication.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.enrichment.adoption.AdoptionEnrichment;
import org.ksmart.birth.birthapplication.model.AdoptionDetail;
import org.ksmart.birth.birthapplication.model.adoption.AdoptionRequest;
import org.ksmart.birth.birthregistry.repository.querybuilder.RegisterQueryBuilder;
import org.ksmart.birth.birthregistry.repository.rowmapper.BirthRegisterRowMapper;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthDeathConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AdoptionRepository {
    private final BndProducer producer;
    private final BirthDeathConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final AdoptionEnrichment adoptionEnrichment;
    private final BirthRegisterRowMapper birthRegisterRowMapper;
    private final RegisterQueryBuilder registerQueryBuilder;

    @Autowired
    AdoptionRepository(JdbcTemplate jdbcTemplate, AdoptionEnrichment adoptionEnrichment,
                       BirthDeathConfiguration birthDeathConfiguration, BndProducer producer,
                       RegisterQueryBuilder registerQueryBuilder, BirthRegisterRowMapper birthRegisterRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.adoptionEnrichment = adoptionEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.registerQueryBuilder = registerQueryBuilder;
        this.birthRegisterRowMapper = birthRegisterRowMapper;
    }

    public List<AdoptionDetail> saveAdoptionDetails(AdoptionRequest request) {
        adoptionEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBirthAdoptionTopic(), request);
        return request.getAdoptionDetails();
    }

    public List<AdoptionDetail> updateAdoptionDetails(AdoptionRequest request) {
        adoptionEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBirthAdoptionTopic(), request);
        return request.getAdoptionDetails();
    }
}
