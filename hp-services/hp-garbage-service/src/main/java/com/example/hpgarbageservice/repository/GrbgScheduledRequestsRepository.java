package com.example.hpgarbageservice.repository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.hpgarbageservice.model.GrbgScheduledRequests;
import com.example.hpgarbageservice.repository.builder.GrbgScheduledRequestsQueryBuilder;

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
