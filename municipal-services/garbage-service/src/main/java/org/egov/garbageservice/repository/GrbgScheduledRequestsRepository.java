package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgScheduledRequests;
import org.egov.garbageservice.repository.builder.GrbgScheduledRequestsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing scheduled garbage request records, supporting CRUD operations via JdbcTemplate.
 */
@Repository
public class GrbgScheduledRequestsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgScheduledRequestsQueryBuilder queryBuilder;

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
     * @param request the request payload containing entity details
     */

    public void create(GrbgScheduledRequests request) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                request.getUuid(),
                request.getGarbageId(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsActive());
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
     * @param request the request payload containing entity details
     */

    public void update(GrbgScheduledRequests request) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                request.getGarbageId(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsActive(),
                request.getUuid());
    }

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @return the output result of type {@link List{@code <GrbgScheduledRequests>}}
     */

    public List<GrbgScheduledRequests> findAll() {
        return jdbcTemplate.query(queryBuilder.SELECT_ALL_QUERY,
                new BeanPropertyRowMapper<>(GrbgScheduledRequests.class));
    }

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param uuid the uuid parameter for this operation
     * @return the output result of type {@link GrbgScheduledRequests}
     */

    public GrbgScheduledRequests findById(String uuid) {
        return jdbcTemplate.queryForObject(queryBuilder.SELECT_BY_ID_QUERY,
                new BeanPropertyRowMapper<>(GrbgScheduledRequests.class),
                uuid);
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
     * @param uuid the uuid parameter for this operation
     */

    public void delete(String uuid) {
        jdbcTemplate.update(queryBuilder.DELETE_QUERY, uuid);
    }
}
