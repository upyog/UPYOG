package org.egov.filemgmnt.repository;

import org.egov.filemgmnt.repository.querybuilder.CommunicationFileManagementQueryBuilder;
import org.egov.filemgmnt.repository.rowmapper.CommunicationFileManagementRowMapper;
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

}
