package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCommercialDetails;
import org.egov.garbageservice.repository.builder.GrbgCommercialDetailsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgCommercialDetailsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCommercialDetailsQueryBuilder queryBuilder;

    public void create(GrbgCommercialDetails grbgCommercialDetails) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCommercialDetails.getUuid(),
                grbgCommercialDetails.getGarbageId(),
                grbgCommercialDetails.getBusinessName(),
                grbgCommercialDetails.getBusinessType(),
                grbgCommercialDetails.getOwnerUserUuid());
    }

    public void update(GrbgCommercialDetails grbgCommercialDetails) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCommercialDetails.getGarbageId(),
                grbgCommercialDetails.getBusinessName(),
                grbgCommercialDetails.getBusinessType(),
                grbgCommercialDetails.getOwnerUserUuid(),
                grbgCommercialDetails.getUuid());
    }
}
