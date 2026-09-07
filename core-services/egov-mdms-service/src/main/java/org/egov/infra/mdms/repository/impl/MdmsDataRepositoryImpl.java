package org.egov.infra.mdms.repository.impl;

import org.egov.infra.mdms.repository.MdmsDataRepository;
import org.egov.infra.mdms.repository.querybuilder.MdmsDataQueryBuilder;
import org.egov.infra.mdms.repository.rowmapper.MdmsDataRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MdmsDataRepositoryImpl implements MdmsDataRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MdmsDataQueryBuilder queryBuilder;
    private final MdmsDataRowMapper rowMapper;

    @Autowired
    public MdmsDataRepositoryImpl(JdbcTemplate jdbcTemplate, MdmsDataQueryBuilder queryBuilder, MdmsDataRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    @Override
    public List<Map<String, Object>> searchAll() {
        String query = queryBuilder.getMdmsDataSearchAllQuery();
        return jdbcTemplate.query(query, rowMapper);
    }

    @Override
    public List<Map<String, Object>> search(String tenantId, String schemaCode, String uniqueIdentifier, String id) {
        java.util.List<Object> preparedStmtList = new java.util.ArrayList<>();
        String query = queryBuilder.getMdmsDataSearchQuery(tenantId, schemaCode, uniqueIdentifier, id, preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}
