package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCollection;
import org.egov.garbageservice.repository.builder.GrbgCollectionQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgCollectionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCollectionQueryBuilder queryBuilder;

    public void create(GrbgCollection grbgCollection) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCollection.getUuid(),
                grbgCollection.getGarbageId(),
                grbgCollection.getStaffUuid(),
                grbgCollection.getCollecType(),
                grbgCollection.getStartDate(),
                grbgCollection.getEndDate(),
                grbgCollection.getIsActive(),
                grbgCollection.getCreatedBy(),
                grbgCollection.getCreatedDate(),
                grbgCollection.getLastModifiedBy(),
                grbgCollection.getLastModifiedDate());
    }

    public void update(GrbgCollection grbgCollection) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCollection.getGarbageId(),
                grbgCollection.getStaffUuid(),
                grbgCollection.getCollecType(),
                grbgCollection.getStartDate(),
                grbgCollection.getEndDate(),
                grbgCollection.getIsActive(),
                grbgCollection.getCreatedBy(),
                grbgCollection.getCreatedDate(),
                grbgCollection.getLastModifiedBy(),
                grbgCollection.getLastModifiedDate(),
                grbgCollection.getUuid());
    }
}
