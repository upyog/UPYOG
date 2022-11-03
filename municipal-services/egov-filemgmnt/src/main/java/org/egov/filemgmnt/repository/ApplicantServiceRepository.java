package org.egov.filemgmnt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.web.models.ApplicantService;
import org.egov.filemgmnt.web.models.ApplicantServiceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicantServiceRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicantServiceQueryBuilder queryBuilder;
    private final ApplicantServiceRowMapper rowMapper;

    @Autowired
    ApplicantServiceRepository(JdbcTemplate jdbcTemplate, ApplicantServiceQueryBuilder queryBuilder,
                               ApplicantServiceRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<ApplicantService> getApplicantServices(ApplicantServiceSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();

        String query = queryBuilder.getApplicantServiceSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);

        List<ApplicantService> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

        return result;
    }

}