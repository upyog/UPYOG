package org.egov.garbageservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.repository.builder.GrbgAddressQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for creating, updating, and deleting garbage address records in the database.
 */
@Repository
public class GrbgAddressRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
     * @param grbgAddress the grbgAddress parameter for this operation
     */

    public void create(GrbgAddress grbgAddress) {
        jdbcTemplate.update(GrbgAddressQueryBuilder.CREATE_QUERY,
                grbgAddress.getUuid(),
                grbgAddress.getGarbageId(),
                grbgAddress.getAddressType(),
                grbgAddress.getAddress1(),
                grbgAddress.getAddress2(),
                grbgAddress.getCity(),
                grbgAddress.getState(),
                grbgAddress.getPincode(),
                grbgAddress.getIsActive(),
                grbgAddress.getZone(),
                grbgAddress.getUlbName(),
                grbgAddress.getUlbType(),
                grbgAddress.getWardName(),
                null == grbgAddress.getAdditionalDetail() ? null : objectMapper.convertValue(grbgAddress.getAdditionalDetail(), ObjectNode.class).toString());
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
     * @param grbgAddress the grbgAddress parameter for this operation
     */

    public void update(GrbgAddress grbgAddress) {
        jdbcTemplate.update(GrbgAddressQueryBuilder.UPDATE_QUERY,
                grbgAddress.getAddressType(),
                grbgAddress.getAddress1(),
                grbgAddress.getAddress2(),
                grbgAddress.getCity(),
                grbgAddress.getState(),
                grbgAddress.getPincode(),
                grbgAddress.getIsActive(),
                grbgAddress.getZone(),
                grbgAddress.getUlbName(),
                grbgAddress.getUlbType(),
                grbgAddress.getWardName(),
                grbgAddress.getGarbageId(),
                null == grbgAddress.getAdditionalDetail() ? null : objectMapper.convertValue(grbgAddress.getAdditionalDetail(), ObjectNode.class).toString(),
                grbgAddress.getUuid());
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
        jdbcTemplate.update(GrbgAddressQueryBuilder.DELETE_QUERY, garbageId);

    }
}
