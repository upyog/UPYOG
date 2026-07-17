package upyog.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import upyog.web.models.Allotment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AllotmentRowmapper implements ResultSetExtractor<List<Allotment>> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Allotment> extractData(ResultSet rs) throws SQLException {
        Map<String, Allotment> allotmentMap = new HashMap<>();
        
        while (rs.next()) {
            String allotmentId = rs.getString("allotment_id");
            Allotment allotment = allotmentMap.get(allotmentId);
            
            if (allotment == null) {
                allotment = new Allotment();
                
                allotment.setAllotmentId(allotmentId);
                allotment.setAssetNo(rs.getString("estate_no"));
                allotment.setTenantId(rs.getString("tenant_id"));
                allotment.setUserUuid(rs.getString("user_uuid"));
                allotment.setAlloteeName(rs.getString("allotee_name"));
                allotment.setMobileNo(rs.getString("mobile_number"));
                allotment.setAlternateMobileNo(rs.getString("alternate_contact_no"));
                allotment.setEmailId(rs.getString("email_id"));
                allotment.setAgreementStartDate(getLocalDate(rs, "agreement_start_date"));
                allotment.setAgreementEndDate(getLocalDate(rs, "agreement_end_date"));
                allotment.setAdvancePaymentDate(getLocalDate(rs, "advance_payment_date"));
                allotment.setDuration(rs.getInt("duration"));
                allotment.setRentRate(getBigDecimal(rs, "rate"));
                allotment.setMonthlyRent(getBigDecimal(rs, "monthly_rent"));
                allotment.setAdvancePayment(getBigDecimal(rs, "advance_payment"));
                allotment.setEofficeFileNo(rs.getString("eoffice_file_no"));
                allotment.setAssetReferenceNo(rs.getString("asset_reference_no"));
                allotment.setPropertyType(rs.getString("property_type"));
                allotment.setCitizenRequestLetter(rs.getString("citizen_request_letter"));
                allotment.setAllotmentLetter(rs.getString("allotment_letter"));
                allotment.setSignedDeed(rs.getString("signed_deed"));
                allotment.setBillingCycle(rs.getString("billing_cycle"));


                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();
                allotment.setAuditDetails(auditDetails);
                
                allotmentMap.put(allotmentId, allotment);
            }
        }
        
        return new ArrayList<>(allotmentMap.values());
    }
    
    /**
     * Helper method to safely get BigDecimal from ResultSet
     */
    private BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Helper method to safely get LocalDate from ResultSet
     */
    private LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName);
        return date != null ? date.toLocalDate() : null;
    }
    
    /**
     * Helper method to parse additional details from JSONB
     * This method is kept for future use when additional_details column is added to allotment table
     */
    @SuppressWarnings("unused")
    private Object getAdditionalDetails(ResultSet rs, String columnName) throws SQLException {
        try {
            PGobject pgObject = (PGobject) rs.getObject(columnName);
            if (pgObject != null) {
                String json = pgObject.getValue();
                if (json != null && !json.isEmpty()) {
                    return objectMapper.readValue(json, JsonNode.class);
                }
            }
        } catch (IOException e) {
            log.error("Error parsing additional details: ", e);
        }
        return null;
    }
}
