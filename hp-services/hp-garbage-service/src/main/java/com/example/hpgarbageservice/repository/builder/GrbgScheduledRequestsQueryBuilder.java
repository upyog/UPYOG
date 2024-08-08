package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgScheduledRequestsQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO grbg_scheduled_requests " +
                                              "(uuid, garbage_id, type, start_date, end_date, is_active) " +
                                              "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE grbg_scheduled_requests " +
                                              "SET garbage_id = ?, type = ?, start_date = ?, end_date = ?, is_active = ? " +
                                              "WHERE uuid = ?";

    public static final String SELECT_ALL_QUERY = "SELECT * FROM grbg_scheduled_requests";

    public static final String SELECT_BY_ID_QUERY = "SELECT * FROM grbg_scheduled_requests WHERE uuid = ?";

    public static final String DELETE_QUERY = "DELETE FROM grbg_scheduled_requests WHERE uuid = ?";
}
