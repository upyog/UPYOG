package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.pt.models.OwnerInfo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class OwnerRowMapper implements RowMapper<OwnerInfo> {

	@Override
	public OwnerInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		OwnerInfo owner = new OwnerInfo();

		owner.setOwnerInfoUuid(rs.getString("ownerinfouuid"));
		owner.setPropertyId(rs.getString("propertyid"));
		owner.setUuid(rs.getString("userid"));
		owner.setName(rs.getString("name"));
		owner.setMobileNumber(rs.getString("mobile_number"));

		return owner;
	}

}