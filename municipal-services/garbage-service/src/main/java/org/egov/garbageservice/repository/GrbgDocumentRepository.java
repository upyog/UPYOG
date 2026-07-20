package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgDocument;
import org.egov.garbageservice.repository.builder.GrbgDocumentQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Repository for persisting and updating garbage document records in the database. */
@Repository
public class GrbgDocumentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgDocumentQueryBuilder queryBuilder;

    public void create(GrbgDocument grbgDocument) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgDocument.getUuid(),
                grbgDocument.getDocumentUid(),
                grbgDocument.getFileStoreId(),
                grbgDocument.getDocumentType(),
                grbgDocument.getTblRefUuid(),
                grbgDocument.getGarbageId());
    }

    public void update(GrbgDocument grbgDocument) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgDocument.getDocumentUid(),
                grbgDocument.getFileStoreId(),
                grbgDocument.getDocumentType(),
                grbgDocument.getTblRefUuid(),
                grbgDocument.getGarbageId(),
                grbgDocument.getUuid());
    }
}