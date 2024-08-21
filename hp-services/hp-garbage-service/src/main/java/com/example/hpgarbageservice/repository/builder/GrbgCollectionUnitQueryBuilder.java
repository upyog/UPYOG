package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgCollectionUnitQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_collection_unit " +
                                              "(uuid, unit_name, unit_ward, ulb_name, type_of_ulb, is_active " 
                                              + ", garbage_id, unit_type, category, sub_category, sub_category_type)"
                                              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_collection_unit " +
                                              "SET unit_name = ?, unit_ward = ?, ulb_name = ?, type_of_ulb = ? " 
                                              + ", garbage_id = ?, unit_type = ?, category = ?, sub_category = ?, sub_category_type = ?, is_active = ? "
                                              + " WHERE uuid = ?";
    
}
