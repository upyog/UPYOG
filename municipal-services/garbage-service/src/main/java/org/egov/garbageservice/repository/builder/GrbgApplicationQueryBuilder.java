package org.egov.garbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgApplicationQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_application " +
                                              "(uuid, application_no, status, garbage_id) " +
                                              "VALUES (?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_application " +
                                              "SET application_no = ?, status = ?, garbage_id = ? " +
                                              "WHERE uuid = ?";
}
