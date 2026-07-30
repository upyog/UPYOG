package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCollectionStaff;
import org.egov.garbageservice.repository.builder.GrbgCollectionStaffQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating and updating garbage collection staff records in the database.
 */
@Repository
public class GrbgCollectionStaffRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCollectionStaffQueryBuilder queryBuilder;

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
     * @param grbgCollectionStaff the grbgCollectionStaff parameter for this operation
     */

    public void create(GrbgCollectionStaff grbgCollectionStaff) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCollectionStaff.getUuid(),
                grbgCollectionStaff.getGrbgCollectionUnitUuid(),
                grbgCollectionStaff.getEmployeeId(),
                grbgCollectionStaff.getRole(),
                grbgCollectionStaff.getIsActive());
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
     * @param grbgCollectionStaff the grbgCollectionStaff parameter for this operation
     */

    public void update(GrbgCollectionStaff grbgCollectionStaff) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCollectionStaff.getGrbgCollectionUnitUuid(),
                grbgCollectionStaff.getEmployeeId(),
                grbgCollectionStaff.getRole(),
                grbgCollectionStaff.getIsActive(),
                grbgCollectionStaff.getUuid());
    }
}
