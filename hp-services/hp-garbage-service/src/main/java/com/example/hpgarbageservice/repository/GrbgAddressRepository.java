package com.example.hpgarbageservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.hpgarbageservice.model.GrbgAddress;
import com.example.hpgarbageservice.repository.builder.GrbgAddressQueryBuilder;

@Repository
public class GrbgAddressRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void create(GrbgAddress grbgAddress) {
        jdbcTemplate.update(GrbgAddressQueryBuilder.CREATE_QUERY,
                grbgAddress.getUuid(),
                grbgAddress.getGarbageId(),
                grbgAddress.getAddressType(),
                grbgAddress.getAddress1(),
                grbgAddress.getAddress2(),
                grbgAddress.getCity(),
                grbgAddress.getState(),
                grbgAddress.getPincode(),
                grbgAddress.getIsActive(),
                grbgAddress.getZone(),
                grbgAddress.getUlbName(),
                grbgAddress.getUlbType(),
                grbgAddress.getWardName());
    }

    public void update(GrbgAddress grbgAddress) {
        jdbcTemplate.update(GrbgAddressQueryBuilder.UPDATE_QUERY,
                grbgAddress.getAddressType(),
                grbgAddress.getAddress1(),
                grbgAddress.getAddress2(),
                grbgAddress.getCity(),
                grbgAddress.getState(),
                grbgAddress.getPincode(),
                grbgAddress.getIsActive(),
                grbgAddress.getZone(),
                grbgAddress.getUlbName(),
                grbgAddress.getUlbType(),
                grbgAddress.getWardName(),
                grbgAddress.getGarbageId(),
                grbgAddress.getUuid());
    }
}
