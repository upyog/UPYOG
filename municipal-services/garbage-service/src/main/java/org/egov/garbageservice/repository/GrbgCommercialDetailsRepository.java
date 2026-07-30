package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCommercialDetails;
import org.egov.garbageservice.repository.builder.GrbgCommercialDetailsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating and updating garbage commercial detail records in the database.
 */
@Repository
public class GrbgCommercialDetailsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCommercialDetailsQueryBuilder queryBuilder;

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
     * @param grbgCommercialDetails the grbgCommercialDetails parameter for this operation
     */

    public void create(GrbgCommercialDetails grbgCommercialDetails) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCommercialDetails.getUuid(),
                grbgCommercialDetails.getGarbageId(),
                grbgCommercialDetails.getBusinessName(),
                grbgCommercialDetails.getBusinessType(),
                grbgCommercialDetails.getOwnerUserUuid());
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
     * @param grbgCommercialDetails the grbgCommercialDetails parameter for this operation
     */

    public void update(GrbgCommercialDetails grbgCommercialDetails) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCommercialDetails.getGarbageId(),
                grbgCommercialDetails.getBusinessName(),
                grbgCommercialDetails.getBusinessType(),
                grbgCommercialDetails.getOwnerUserUuid(),
                grbgCommercialDetails.getUuid());
    }
}
