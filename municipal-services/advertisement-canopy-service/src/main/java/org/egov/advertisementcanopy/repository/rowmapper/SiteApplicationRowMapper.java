package org.egov.advertisementcanopy.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SiteApplicationRowMapper implements ResultSetExtractor<List<SiteCreationData>> {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public List<SiteCreationData> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<Long, SiteCreationData> accountsMap = new LinkedHashMap<>();
		JsonNode additinalDetail = null;
		try {
			while (rs.next()) {
				Long accountId = rs.getLong("id");
				SiteCreationData siteApplication = accountsMap.get(accountId);
				if (null == siteApplication) {
					AuditDetails auditDetail = AuditDetails.builder().createdBy(rs.getString("created_by"))
							.createdDate(rs.getLong("created_date")).lastModifiedBy(rs.getString("last_modified_by"))
							.lastModifiedDate(rs.getLong("last_modified_date")).build();
					
					SiteCreationData siteCreationData = SiteCreationData.builder()
							.id(rs.getLong("id"))
						    .uuid(rs.getString("uuid"))
						    .siteID(rs.getString("site_id"))
						    .siteName(rs.getString("site_name"))
						    .siteDescription(rs.getString("site_description"))
						    .gpsLocation(rs.getString("gps_location"))
						    .siteAddress(rs.getString("site_address"))
						    .siteCost(rs.getString("site_cost"))
						    .sitePhotograph(rs.getString("site_photograph"))
						    .structure(rs.getString("structure"))
						    .sizeLength(rs.getLong("size_length"))
						    .sizeWidth(rs.getLong("size_width"))
						    .ledSelection(rs.getString("led_selection"))
						    .securityAmount(rs.getLong("security_amount"))
						    .powered(rs.getString("powered"))
						    .others(rs.getString("others"))
						    .districtName(rs.getString("district_name"))
						    .ulbName(rs.getString("ulb_name"))
						    .ulbType(rs.getString("ulb_type"))
						    .wardNumber(rs.getString("ward_number"))
						    .pinCode(rs.getString("pincode"))
	                        .additionalDetail(getAdditionalDetail(rs, "additional_detail"))
						    .auditDetails(auditDetail)
						    .siteType(rs.getString("site_type"))
						    .accountId(rs.getString("account_id"))
						    .status(rs.getString("status"))
						    .isActive(rs.getBoolean("is_active"))
						    .tenantId(rs.getString("tenant_id"))
						    .applicationStartDate(rs.getLong("application_start_date"))
						    .applicationEndDate(rs.getLong("application_end_date"))
						    .bookingPeriodStartDate(rs.getLong("booking_start_date"))
						    .bookingPeriodEndDate(rs.getLong("booking_end_date"))
						    .workFlowStatus(rs.getString("workflow_status"))
						    .build();
					
					
					accountsMap.put(accountId, siteCreationData);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return new ArrayList<>(accountsMap.values());
	}
	
	private JsonNode getAdditionalDetail(ResultSet rs, String columnLabel) {
    	JsonNode jsonNode = null;
    	try {
    		String jsonString = rs.getString(columnLabel);
            if (jsonString != null) {
                jsonNode = objectMapper.readTree(jsonString);
            }
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}

}
