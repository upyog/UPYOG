package org.egov.filemgmnt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.repository.querybuilder.CommunicationFileManagementQueryBuilder;
import org.egov.filemgmnt.repository.rowmapper.CommunicationFileManagementRowMapper;
import org.egov.filemgmnt.web.models.CommunicationFile;
import org.egov.filemgmnt.web.models.CommunicationFileSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommunicationFileManagementRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommunicationFileManagementQueryBuilder queryBuilder;
    private final CommunicationFileManagementRowMapper rowMapper;

    @Autowired
    CommunicationFileManagementRepository(JdbcTemplate jdbcTemplate,
                                          CommunicationFileManagementQueryBuilder queryBuilder,
                                          CommunicationFileManagementRowMapper rowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<CommunicationFile> getCommunicationfiles(CommunicationFileSearchCriteria criteria) {

        List<Object> preparedStmtValues = new ArrayList<>();

        String query = queryBuilder.getCommunicationFileSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);

        List<CommunicationFile> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

        return result;

    }
}
