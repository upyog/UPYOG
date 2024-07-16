package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgOldDetailsQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO grbg_old_details " +
                                              "(uuid, garbage_id, old_garbage_id) " +
                                              "VALUES (?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE grbg_old_details " +
                                              "SET garbage_id = ?, old_garbage_id = ? " +
                                              "WHERE uuid = ?";
}
