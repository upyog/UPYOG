package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgAddressQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO grbg_address (uuid, address_type, address1, address2, city, state, pincode, is_active) " +
                                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE grbg_address " +
                                               "SET address_type = ?, address1 = ?, address2 = ?, city = ?, state = ?, pincode = ?, is_active = ? " +
                                               "WHERE uuid = ?";
}
