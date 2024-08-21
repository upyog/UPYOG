package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgChargeQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_charge (uuid, category, type, amount_per_day, amount_pm, is_active) " +
                                               "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_charge " +
                                               "SET category = ?, type = ?, amount_per_day = ?, amount_pm = ?, is_active = ? " +
                                               "WHERE uuid = ?";
}
