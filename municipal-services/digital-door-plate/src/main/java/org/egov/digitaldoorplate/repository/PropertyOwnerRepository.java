package org.egov.digitaldoorplate.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PropertyOwnerRepository {

	/**
	 * property-services' /property/_search overwrites owners[].mobileNumber with
	 * egov-user's stored number, whereas the number encoded on the door plate
	 * QR comes from eg_pt_owner.mobile_number itself, so that column is read
	 * directly (both services share the same physical database).
	 */
	private static final String SELECT_OWNER_MOBILE_NUMBERS = "SELECT o.mobile_number AS mobile_number "
			+ "FROM eg_pt_property p JOIN eg_pt_owner o ON o.propertyid = p.id "
			+ "WHERE p.propertyid = ? AND p.tenantid = ? AND o.status = 'ACTIVE' "
			+ "ORDER BY o.isprimaryowner DESC NULLS LAST";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Mobile number of the property's primary owner, falling back to any other
	 * active owner's number when there is no primary owner; null if none.
	 */
	public String getOwnerMobileNumber(String propertyId, String tenantId) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(SELECT_OWNER_MOBILE_NUMBERS, propertyId, tenantId);
		return rows.stream().map(row -> (String) row.get("mobile_number")).filter(number -> null != number)
				.findFirst().orElse(null);
	}
}
