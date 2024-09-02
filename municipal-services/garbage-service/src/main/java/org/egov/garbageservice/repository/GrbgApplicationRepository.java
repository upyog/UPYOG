package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgApplication;
import org.egov.garbageservice.repository.builder.GrbgApplicationQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgApplicationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgApplicationQueryBuilder queryBuilder;

    public void create(GrbgApplication grbgApplication) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgApplication.getUuid(),
                grbgApplication.getApplicationNo(),
                grbgApplication.getStatus(),
                grbgApplication.getGarbageId());
    }

    public void update(GrbgApplication grbgApplication) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgApplication.getApplicationNo(),
                grbgApplication.getStatus(),
                grbgApplication.getGarbageId(),
                grbgApplication.getUuid());
    }
}
