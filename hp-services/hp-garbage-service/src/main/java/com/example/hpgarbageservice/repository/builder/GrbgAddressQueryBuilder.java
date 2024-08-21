package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgAddressQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_address (uuid, garbage_id, address_type, address1, address2, city, state, pincode, is_active, zone, ulb_name, ulb_type, ward_name, additional_detail) " +
                                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? :: JSONB)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_address " 
    								+ " SET address_type = ?, address1 = ?, address2 = ?, city = ?, state = ?, pincode = ?, is_active = ? " 
    								+ ", zone = ?, ulb_name = ?, ulb_type = ?, ward_name = ?, garbage_id = ?, additional_detail = ? :: JSONB "
    								+ " WHERE uuid = ?";
}
