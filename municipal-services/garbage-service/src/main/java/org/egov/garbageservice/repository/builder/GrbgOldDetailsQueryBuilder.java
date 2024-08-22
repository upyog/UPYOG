package org.egov.garbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgOldDetailsQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_old_details " +
                                              "(uuid, garbage_id, old_garbage_id) " +
                                              "VALUES (?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_old_details " +
                                              "SET garbage_id = ?, old_garbage_id = ? " +
                                              "WHERE uuid = ?";
}
