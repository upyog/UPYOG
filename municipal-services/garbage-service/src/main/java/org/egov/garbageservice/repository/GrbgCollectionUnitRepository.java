package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgCollectionUnit;
import org.egov.garbageservice.repository.builder.GrbgCollectionUnitQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating, updating, and deleting garbage collection unit records in the database.
 */
@Repository
public class GrbgCollectionUnitRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgCollectionUnitQueryBuilder queryBuilder;

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
     * @param grbgCollectionUnit the grbgCollectionUnit parameter for this operation
     */

    public void create(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                grbgCollectionUnit.getUuid(),
                grbgCollectionUnit.getUnitName(),
                grbgCollectionUnit.getUnitWard(),
                grbgCollectionUnit.getUlbName(),
                grbgCollectionUnit.getTypeOfUlb(),
                grbgCollectionUnit.getIsActive(),
                grbgCollectionUnit.getGarbageId(),
                grbgCollectionUnit.getUnitType(),
                grbgCollectionUnit.getCategory(),
                grbgCollectionUnit.getSubCategory(),
                grbgCollectionUnit.getSubCategoryType(),
                grbgCollectionUnit.getIsbplunit(),
                grbgCollectionUnit.getIsvariablecalculation(),
                grbgCollectionUnit.getIsbulkgeneration(),
                grbgCollectionUnit.getNo_of_units(),
                grbgCollectionUnit.getIsmonthlybilling(),
                grbgCollectionUnit.getOwnerType(),
                grbgCollectionUnit.getIsInheritance(),
                grbgCollectionUnit.getSpecialCategory());
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
     * @param grbgCollectionUnit the grbgCollectionUnit parameter for this operation
     */

    public void update(GrbgCollectionUnit grbgCollectionUnit) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                grbgCollectionUnit.getUnitName(),
                grbgCollectionUnit.getUnitWard(),
                grbgCollectionUnit.getUlbName(),
                grbgCollectionUnit.getTypeOfUlb(),
                grbgCollectionUnit.getGarbageId(),
                grbgCollectionUnit.getUnitType(),
                grbgCollectionUnit.getCategory(),
                grbgCollectionUnit.getSubCategory(),
                grbgCollectionUnit.getSubCategoryType(),
                grbgCollectionUnit.getIsActive(),
                grbgCollectionUnit.getIsbplunit(),
                grbgCollectionUnit.getIsbulkgeneration(),
                grbgCollectionUnit.getIsvariablecalculation(),
                grbgCollectionUnit.getNo_of_units(),
                grbgCollectionUnit.getIsmonthlybilling(),
                grbgCollectionUnit.getUuid(),
                grbgCollectionUnit.getOwnerType(),
                grbgCollectionUnit.getIsInheritance(),
                grbgCollectionUnit.getSpecialCategory());
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
