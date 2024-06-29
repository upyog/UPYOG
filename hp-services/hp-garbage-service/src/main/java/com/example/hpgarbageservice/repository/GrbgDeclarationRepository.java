package com.example.hpgarbageservice.repository;

import com.example.hpgarbageservice.model.GrbgDeclaration;
import com.example.hpgarbageservice.repository.builder.GrbgDeclarationQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgDeclarationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgDeclarationQueryBuilder queryBuilder;

    public void create(GrbgDeclaration grbgDeclaration) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgDeclaration.getUuid(),
                grbgDeclaration.getStatement(),
                grbgDeclaration.getIsActive());
    }

    public void update(GrbgDeclaration grbgDeclaration) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgDeclaration.getStatement(),
                grbgDeclaration.getIsActive(),
                grbgDeclaration.getUuid());
    }
}
