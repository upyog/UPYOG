package org.egov.garbageservice.repository;
import java.util.List;

import org.egov.garbageservice.model.GrbgScheduledRequests;
import org.egov.garbageservice.repository.builder.GrbgScheduledRequestsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GrbgScheduledRequestsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GrbgScheduledRequestsQueryBuilder queryBuilder;

    public void create(GrbgScheduledRequests request) {
        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
                request.getUuid(),
                request.getGarbageId(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsActive());
    }

    public void update(GrbgScheduledRequests request) {
        jdbcTemplate.update(queryBuilder.UPDATE_QUERY,
                request.getGarbageId(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsActive(),
                request.getUuid());
    }

    public List<GrbgScheduledRequests> findAll() {
        return jdbcTemplate.query(queryBuilder.SELECT_ALL_QUERY,
                new BeanPropertyRowMapper<>(GrbgScheduledRequests.class));
    }

    public GrbgScheduledRequests findById(String uuid) {
        return jdbcTemplate.queryForObject(queryBuilder.SELECT_BY_ID_QUERY,
                new BeanPropertyRowMapper<>(GrbgScheduledRequests.class),
                uuid);
    }

    public void delete(String uuid) {
        jdbcTemplate.update(queryBuilder.DELETE_QUERY, uuid);
    }
}
