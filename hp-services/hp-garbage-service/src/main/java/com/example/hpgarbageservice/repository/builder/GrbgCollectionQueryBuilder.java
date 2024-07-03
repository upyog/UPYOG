package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgCollectionQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO grbg_collection " +
                                              "(uuid, garbage_id, staff_uuid, collec_type, start_date, end_date, is_active, createdby, createddate, lastmodifiedby, lastmodifieddate) " +
                                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE grbg_collection " +
                                              "SET garbage_id = ?, staff_uuid = ?, collec_type = ?, start_date = ?, end_date = ?, is_active = ?, " +
                                              "createdby = ?, createddate = ?, lastmodifiedby = ?, lastmodifieddate = ? " +
                                              "WHERE uuid = ?";
}
