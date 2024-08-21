package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgCollectionStaffQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_collection_staff " +
                                              "(uuid, grbg_collection_unit_uuid, employee_id, role, is_active) " +
                                              "VALUES (?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_collection_staff " +
                                              "SET grbg_collection_unit_uuid = ?, employee_id = ?, role = ?, is_active = ? " +
                                              "WHERE uuid = ?";
}
