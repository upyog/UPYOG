package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCharge;
import org.egov.garbageservice.repository.builder.GrbgChargeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgChargeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgChargeQueryBuilder queryBuilder;

    public void create(GrbgCharge grbgCharge) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCharge.getUuid(),
                grbgCharge.getCategory(),
                grbgCharge.getType(),
                grbgCharge.getAmountPerDay(),
                grbgCharge.getAmountPm(),
                grbgCharge.getIsActive());
    }

    public void update(GrbgCharge grbgCharge) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCharge.getCategory(),
                grbgCharge.getType(),
                grbgCharge.getAmountPerDay(),
                grbgCharge.getAmountPm(),
                grbgCharge.getIsActive(),
                grbgCharge.getUuid());
    }
}
