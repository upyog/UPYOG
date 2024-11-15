package org.egov.pqm.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.pqmProducer.PqmProducer;
import org.egov.pqm.repository.querybuilder.PlantUserQueryBuilder;
import org.egov.pqm.repository.rowmapper.PlantUserRowMapper;
import org.egov.pqm.web.model.plant.user.PlantUser;
import org.egov.pqm.web.model.plant.user.PlantUserRequest;
import org.egov.pqm.web.model.plant.user.PlantUserResponse;
import org.egov.pqm.web.model.plant.user.PlantUserSearchRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class PlantUserRepository {

    private final PlantUserQueryBuilder plantUserQueryBuilder;
    private final PlantUserRowMapper plantUserRowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final PqmProducer pqmProducer;
    private final ServiceConfiguration serviceConfig;

    public PlantUserRepository(PlantUserQueryBuilder plantUserQueryBuilder, PlantUserRowMapper plantUserRowMapper,
                               JdbcTemplate jdbcTemplate, PqmProducer pqmProducer,
                               ServiceConfiguration serviceConfig) {
        this.plantUserQueryBuilder = plantUserQueryBuilder;
        this.plantUserRowMapper = plantUserRowMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.pqmProducer = pqmProducer;
        this.serviceConfig = serviceConfig;
    }

    public List<PlantUser> save(PlantUserRequest plantUserRequest) {
        pqmProducer.push(serviceConfig.getPlantUserSaveTopic(), plantUserRequest);
        return plantUserRequest.getPlantUsers();
    }

    public List<PlantUser> update(PlantUserRequest plantUserRequest) {
        pqmProducer.push(serviceConfig.getPlantUserUpdateTopic(), plantUserRequest);
        return plantUserRequest.getPlantUsers();
    }

    public PlantUserResponse search(PlantUserSearchRequest plantUserSearchRequest) {
        List<Object> preparedStatementData = new ArrayList<>();
        String searchQuery = plantUserQueryBuilder.getSearchQuery(plantUserSearchRequest, preparedStatementData);
        List<PlantUser> plantUsers = jdbcTemplate.query(searchQuery, preparedStatementData.toArray(), plantUserRowMapper);
        return PlantUserResponse.builder().plantUsers(plantUsers).totalCount(plantUserRowMapper.getTotalCount()).build();
    }
}
