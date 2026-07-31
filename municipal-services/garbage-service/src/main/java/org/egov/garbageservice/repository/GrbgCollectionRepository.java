package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCollection;
import org.egov.garbageservice.repository.builder.GrbgCollectionQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating and updating garbage collection records in the database.
 */
@Repository
public class GrbgCollectionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCollectionQueryBuilder queryBuilder;

    /**
     * Persists a new entity record into the database.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates the incoming request payload and audit details.</li>
     *   <li>Constructs the parameterized SQL insert query for entity persistence.</li>
     *   <li>Executes the database insert using {@link org.springframework.jdbc.core.JdbcTemplate}.</li>
     *   <li>Returns the created entity instance with populated audit metadata.</li>
     * </ol>
     *
     * @param grbgCollection the grbgCollection parameter for this operation
     */

    public void create(GrbgCollection grbgCollection) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCollection.getUuid(),
                grbgCollection.getGarbageId(),
                grbgCollection.getStaffUuid(),
                grbgCollection.getCollecType(),
                grbgCollection.getStartDate(),
                grbgCollection.getEndDate(),
                grbgCollection.getIsActive(),
                grbgCollection.getCreatedBy(),
                grbgCollection.getCreatedDate(),
                grbgCollection.getLastModifiedBy(),
                grbgCollection.getLastModifiedDate());
    }

    /**
     * Updates existing entity details in the persistent repository.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Extracts updated entity attributes and audit timestamps.</li>
     *   <li>Constructs the parameterized SQL update query.</li>
     *   <li>Executes the update statement against the persistent store.</li>
     *   <li>Returns the modified entity state.</li>
     * </ol>
     *
     * @param grbgCollection the grbgCollection parameter for this operation
     */

    public void update(GrbgCollection grbgCollection) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCollection.getGarbageId(),
                grbgCollection.getStaffUuid(),
                grbgCollection.getCollecType(),
                grbgCollection.getStartDate(),
                grbgCollection.getEndDate(),
                grbgCollection.getIsActive(),
                grbgCollection.getCreatedBy(),
                grbgCollection.getCreatedDate(),
                grbgCollection.getLastModifiedBy(),
                grbgCollection.getLastModifiedDate(),
                grbgCollection.getUuid());
    }
}
