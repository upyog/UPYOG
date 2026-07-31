package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCharge;
import org.egov.garbageservice.repository.builder.GrbgChargeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating and updating garbage charge records in the database.
 */
@Repository
public class GrbgChargeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgChargeQueryBuilder queryBuilder;

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
     * @param grbgCharge the grbgCharge parameter for this operation
     */

    public void create(GrbgCharge grbgCharge) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCharge.getUuid(),
                grbgCharge.getCategory(),
                grbgCharge.getType(),
                grbgCharge.getAmountPerDay(),
                grbgCharge.getAmountPm(),
                grbgCharge.getIsActive());
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
     * @param grbgCharge the grbgCharge parameter for this operation
     */

    public void update(GrbgCharge grbgCharge) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCharge.getCategory(),
                grbgCharge.getType(),
                grbgCharge.getAmountPerDay(),
                grbgCharge.getAmountPm(),
                grbgCharge.getIsActive(),
                grbgCharge.getUuid());
    }
}
