package org.egov.garbageservice.repository;

import org.egov.garbageservice.web.models.GrbgOldDetails;
import org.egov.garbageservice.repository.builder.GrbgOldDetailsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating, updating, and deleting old garbage ID mapping records in the database.
 */
@Repository
public class GrbgOldDetailsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgOldDetailsQueryBuilder queryBuilder;

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
     * @param grbgOldDetails the grbgOldDetails parameter for this operation
     */

    public void create(GrbgOldDetails grbgOldDetails) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgOldDetails.getUuid(),
                grbgOldDetails.getGarbageId(),
                grbgOldDetails.getOldGarbageId());
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
     * @param grbgOldDetails the grbgOldDetails parameter for this operation
     */

    public void update(GrbgOldDetails grbgOldDetails) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgOldDetails.getGarbageId(),
                grbgOldDetails.getOldGarbageId(),
                grbgOldDetails.getUuid());
    }

    /**
     * Executes the delete database operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates method parameters.</li>
     *   <li>Executes repository database operation.</li>
     *   <li>Processes and returns the resulting output.</li>
     * </ol>
     *
     * @param garbageId the garbageId parameter for this operation
     */

    public void delete(Long garbageId) {
        jdbcTemplate.update(queryBuilder.DELETE_QUERY, garbageId);
    }
}
