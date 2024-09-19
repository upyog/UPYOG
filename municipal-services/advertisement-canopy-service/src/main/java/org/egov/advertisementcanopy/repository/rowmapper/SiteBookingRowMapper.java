package org.egov.advertisementcanopy.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SiteBookingRowMapper implements RowMapper<SiteBooking> {

	@Autowired
	private ObjectMapper objectMapper;
	
    @Override
    public SiteBooking mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
        AuditDetails audit = AuditDetails.builder()
                .createdBy(rs.getString("created_by"))
                .createdDate(rs.getLong("created_date"))
                .lastModifiedBy(rs.getString("last_modified_by"))
                .lastModifiedDate(rs.getLong("last_modified_date"))
                .build();
        
        return SiteBooking.builder()
                .uuid(rs.getString("uuid"))
                .applicationNo(rs.getString("application_no"))
                .siteUuid(rs.getString("site_uuid"))
                .applicantName(rs.getString("applicant_name"))
                .applicantFatherName(rs.getString("applicant_father_name"))
                .gender(rs.getString("gender"))
                .mobileNumber(rs.getString("mobile_number"))
                .emailId(rs.getString("email_id"))
                .advertisementType(rs.getString("advertisement_type"))
                .fromDate(rs.getLong("from_date"))
                .periodInDays(rs.getLong("period_in_days"))
                .hoardingType(rs.getString("hoarding_type"))
                .structure(rs.getString("structure"))
                .tenantId(rs.getString("tenant_id"))
                .status(rs.getString("status"))
                .isActive(rs.getBoolean("is_active"))
                .additionalDetail(getAdditionalDetail(rs, "additional_detail"))
                .auditDetails(audit)
                .build();
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