package com.example.hpgarbageservice.repository;

import com.example.hpgarbageservice.model.GrbgOldDetails;
import com.example.hpgarbageservice.repository.builder.GrbgOldDetailsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgOldDetailsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgOldDetailsQueryBuilder queryBuilder;

    public void create(GrbgOldDetails grbgOldDetails) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgOldDetails.getUuid(),
                grbgOldDetails.getGarbageId(),
                grbgOldDetails.getOldGarbageId());
    }

    public void update(GrbgOldDetails grbgOldDetails) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgOldDetails.getGarbageId(),
                grbgOldDetails.getOldGarbageId(),
                grbgOldDetails.getUuid());
    }
}
