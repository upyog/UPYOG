package org.egov.filemgmnt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.repository.querybuilder.ServiceDetailsQueryBuilder;
import org.egov.filemgmnt.repository.rowmapper.ServiceDetailsRowMapper;
import org.egov.filemgmnt.web.models.ServiceDetails;
import org.egov.filemgmnt.web.models.ServiceDetailsSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceDetailsQueryBuilder queryBuilder;
    private final ServiceDetailsRowMapper rowMapper;

    @Autowired
    ServiceDetailsRepository(JdbcTemplate jdbcTemplate, ServiceDetailsQueryBuilder queryBuilder,
                             ServiceDetailsRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<ServiceDetails> getApplicantServices(ServiceDetailsSearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();

        String query = queryBuilder.getServiceDetailsSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);

        List<ServiceDetails> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

        return result; // NOPMD
    }

}
