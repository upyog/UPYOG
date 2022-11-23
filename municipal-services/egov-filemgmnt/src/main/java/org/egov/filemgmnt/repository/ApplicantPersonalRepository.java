package org.egov.filemgmnt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.repository.querybuilder.ApplicantPersonalQueryBuilder;
import org.egov.filemgmnt.repository.rowmapper.ApplicantPersonalRowMapper;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicantPersonalRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicantPersonalQueryBuilder queryBuilder;
    private final ApplicantPersonalRowMapper rowMapper;

    @Autowired
    ApplicantPersonalRepository(JdbcTemplate jdbcTemplate, ApplicantPersonalQueryBuilder queryBuilder,
                                ApplicantPersonalRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<ApplicantPersonal> getApplicantPersonals(ApplicantPersonalSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = queryBuilder.getApplicantPersonalSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);

        List<ApplicantPersonal> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

        return result; // NOPMD
    }
}
