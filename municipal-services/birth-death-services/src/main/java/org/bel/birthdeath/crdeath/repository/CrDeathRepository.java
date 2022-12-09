package org.bel.birthdeath.crdeath.repository;

import java.util.ArrayList;
import java.util.List;

import org.bel.birthdeath.crdeath.repository.querybuilder.CrDeathQueryBuilder;
import org.bel.birthdeath.crdeath.repository.rowmapper.CrDeathRowMapper;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on 05.12.2022
     */

@Repository
public class CrDeathRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final CrDeathQueryBuilder queryBuilder;
    private final CrDeathRowMapper rowMapper;

    @Autowired
    CrDeathRepository(JdbcTemplate jdbcTemplate, CrDeathQueryBuilder queryBuilder,
                        CrDeathRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<CrDeathDtl> getDeathApplication(CrDeathSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = queryBuilder.getDeathSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        System.out.println("RakhiQuery" + query);
        List<CrDeathDtl> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
        return result; // NOPMD
    }
}
