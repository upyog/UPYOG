package com.example.hpgarbageservice.repository;

import com.example.hpgarbageservice.model.GrbgCollectionUnit;
import com.example.hpgarbageservice.repository.builder.GrbgCollectionUnitQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgCollectionUnitRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCollectionUnitQueryBuilder queryBuilder;

    public void create(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCollectionUnit.getUuid(),
                grbgCollectionUnit.getUnitName(),
                grbgCollectionUnit.getUnitWard(),
                grbgCollectionUnit.getUlbName(),
                grbgCollectionUnit.getTypeOfUlb());
    }

    public void update(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCollectionUnit.getUnitName(),
                grbgCollectionUnit.getUnitWard(),
                grbgCollectionUnit.getUlbName(),
                grbgCollectionUnit.getTypeOfUlb(),
                grbgCollectionUnit.getUuid());
    }

    public void deactivate(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.DEACTIVATE_QUERY,
                grbgCollectionUnit.getIsActive(),
                grbgCollectionUnit.getUuid());
    }
}
