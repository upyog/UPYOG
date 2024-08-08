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
                grbgCollectionUnit.getTypeOfUlb(),
                grbgCollectionUnit.getIsActive(),
                grbgCollectionUnit.getGarbageId(),
                grbgCollectionUnit.getUnitType(),
                grbgCollectionUnit.getCategory(),
                grbgCollectionUnit.getSubCategory(),
                grbgCollectionUnit.getSubCategoryType());
    }


    public void update(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
        		grbgCollectionUnit.getUnitName(),
                grbgCollectionUnit.getUnitWard(),
                grbgCollectionUnit.getUlbName(),
                grbgCollectionUnit.getTypeOfUlb(),
                grbgCollectionUnit.getGarbageId(),
                grbgCollectionUnit.getUnitType(),
                grbgCollectionUnit.getCategory(),
                grbgCollectionUnit.getSubCategory(),
                grbgCollectionUnit.getSubCategoryType(),
                grbgCollectionUnit.getIsActive(),
                grbgCollectionUnit.getUuid());
    }

}
