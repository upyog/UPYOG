package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgCommercialDetailsQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO grbg_commercial_details " +
                                              "(uuid, garbage_id, business_name, business_type, owner_user_uuid) " +
                                              "VALUES (?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE grbg_commercial_details " +
                                              "SET business_name = ?, business_type = ?, owner_user_uuid = ? " +
                                              "WHERE uuid = ?";
}
