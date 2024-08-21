package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgDeclarationQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_declaration " +
                                              "(uuid, statement, is_active) " +
                                              "VALUES (?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_declaration " +
                                              "SET statement = ?, is_active = ? " +
                                              "WHERE uuid = ?";
}
