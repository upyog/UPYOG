package org.egov.garbageservice.repository;

import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.repository.builder.GrbgAddressQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
public class GrbgAddressRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
                grbgAddress.getWardName(),
                null == grbgAddress.getAdditionalDetail() ? null : objectMapper.convertValue(grbgAddress.getAdditionalDetail(), ObjectNode.class).toString());
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
                null == grbgAddress.getAdditionalDetail() ? null : objectMapper.convertValue(grbgAddress.getAdditionalDetail(), ObjectNode.class).toString(),
                grbgAddress.getUuid());
    }
}
