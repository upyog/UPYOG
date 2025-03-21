package org.egov.user.repository.rowmapper;

import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.enums.AddressType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AddressRowMapperV2 implements RowMapper<Address> {

    @Override
    public Address mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        final Address address = Address.builder().id(rs.getLong("id")).addressType(rs.getString("type")).address(rs.getString("address"))
                .city(rs.getString("city")).pinCode(rs.getString("pincode")).userId(rs.getLong("userid")).tenantId(rs.getString("tenantid"))
                .address2(rs.getString("address2")).houseNumber(rs.getString("houseNumber")).houseName(rs.getString("houseName"))
                .streetName(rs.getString("streetName")).landmark(rs.getString("landmark")).locality(rs.getString("locality")).build();

        for (AddressType addressType : AddressType.values()) {
            if (addressType.toString().equals(rs.getString("type"))) {
                address.setType(addressType);
            }
        }
        return address;
    }
}


