package org.egov.advertisementcanopy.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteCreationData;
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
        
        SiteBooking siteBooking = SiteBooking.builder()
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
                .toDate(rs.getLong("to_date"))
                .periodInDays(rs.getLong("period_in_days"))
                .hoardingType(rs.getString("hoarding_type"))
                .structure(rs.getString("structure"))
                .tenantId(rs.getString("tenant_id"))
                .status(rs.getString("status"))
                .isActive(rs.getBoolean("is_active"))
                .additionalDetail(getAdditionalDetail(rs, "additional_detail"))
                .auditDetails(audit)
                .build();
        
        if (null != rs.getString("site_uuid")
        		&& null == siteBooking.getSiteCreationData()) {
        	SiteCreationData siteCreationData = populateSiteCreationData(rs, "site_");
        	siteBooking.setSiteCreationData(siteCreationData);
        }
        
        return siteBooking;
        
    }
    
    private SiteCreationData populateSiteCreationData(ResultSet rs, String prefix) throws SQLException {
        return SiteCreationData.builder()
                .id(rs.getLong(prefix + "id"))
                .uuid(rs.getString(prefix + "uuid"))
                .siteID(rs.getString(prefix + "site_id"))
                .siteName(rs.getString(prefix + "site_name"))
                .siteDescription(rs.getString(prefix + "site_description"))
                .siteCost(rs.getString(prefix + "site_cost"))
                .tenantId(rs.getString(prefix + "tenant_id"))
                .gpsLocation(rs.getString(prefix + "gps_location"))
                .siteAddress(rs.getString(prefix + "site_address"))
                .sitePhotograph(rs.getString(prefix + "site_photograph"))
                .structure(rs.getString(prefix + "structure"))
                .sizeLength(rs.getLong(prefix + "size_length"))
                .sizeWidth(rs.getLong(prefix + "size_width"))
                .ledSelection(rs.getString(prefix + "led_selection"))
                .securityAmount(rs.getLong(prefix + "security_amount"))
                .powered(rs.getString(prefix + "powered"))
                .others(rs.getString(prefix + "others"))
                .districtName(rs.getString(prefix + "district_name"))
                .ulbName(rs.getString(prefix + "ulb_name"))
                .ulbType(rs.getString(prefix + "ulb_type"))
                .wardNumber(rs.getString(prefix + "ward_number"))
                .pinCode(rs.getString(prefix + "pincode"))
//                .additionalDetail(rs.getObject(prefix + "additional_detail", JsonNode.class)) // Assuming JsonNode can be fetched like this
                .siteType(rs.getString(prefix + "site_type"))
                .isActive(rs.getBoolean(prefix + "is_active"))
                .status(rs.getString(prefix + "status"))
                .applicationStartDate(rs.getLong(prefix + "application_start_date"))
                .applicationEndDate(rs.getLong(prefix + "application_end_date"))
                .bookingPeriodStartDate(rs.getLong(prefix + "booking_start_date"))
                .bookingPeriodEndDate(rs.getLong(prefix + "booking_end_date"))
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