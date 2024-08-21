package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgDocument;
import org.egov.garbageservice.repository.builder.GrbgDocumentQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgDocumentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgDocumentQueryBuilder queryBuilder;

    public void create(GrbgDocument grbgDocument) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgDocument.getUuid(),
                grbgDocument.getDocRefId(),
                grbgDocument.getDocName(),
                grbgDocument.getDocType(),
                grbgDocument.getDocCategory(),
                grbgDocument.getTblRefUuid());
    }

    public void update(GrbgDocument grbgDocument) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgDocument.getDocRefId(),
                grbgDocument.getDocName(),
                grbgDocument.getDocType(),
                grbgDocument.getDocCategory(),
                grbgDocument.getTblRefUuid(),
                grbgDocument.getUuid());
    }
}
