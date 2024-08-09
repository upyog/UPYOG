package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.models.Address;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import digit.web.models.SchemeApplication;

@Component
public class SchemeApplicationRowMapper implements ResultSetExtractor<List<SchemeApplication>> {

    @Override
    public List<SchemeApplication> extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
        // Map to hold SchemeApplication objects with their IDs as keys
        Map<Long, SchemeApplication> schemeApplicationMap = new LinkedHashMap<>();

        // Iterate through the ResultSet
        while (rs.next()) {
            Long id = rs.getLong("usa_id");
            SchemeApplication schemeApplication = schemeApplicationMap.get(id);

            // If the SchemeApplication object is not already in the map, create it
            if (schemeApplication == null) {
                Long lastModifiedTime = rs.getLong("usa_ModifiedOn");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }

                // Create User object with all user details
                User user = User.builder()
                        .id(rs.getLong("usa_userid"))
                        .uuid(rs.getString("user_uuid"))
                        .userName(rs.getString("user_username"))
                        .name(rs.getString("user_name"))
                        .emailId(rs.getString("user_emailid"))
                        .mobileNumber(rs.getString("user_mobilenumber"))
                        .build();

                // Create AuditDetails object
                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("usa_CreatedBy"))
                        .createdTime(rs.getLong("usa_CreatedOn"))
                        .lastModifiedBy(rs.getString("usa_ModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                // Create SchemeApplication object
                schemeApplication = SchemeApplication.builder()
                        .id(rs.getLong("usa_id"))
                        .applicationNumber(rs.getString("usa_applicationNumber"))
                        .userId(rs.getLong("usa_userid"))
                        .tenantId(rs.getString("usa_tenantid"))
                        .optedId(rs.getLong("usa_optedId"))
                        .applicationStatus(rs.getBoolean("usa_ApplicationStatus"))
                        .verificationStatus(rs.getBoolean("usa_VerificationStatus"))
                        .firstApprovalStatus(rs.getBoolean("usa_FirstApprovalStatus"))
                        .randomSelection(rs.getBoolean("usa_RandomSelection"))
                        .finalApproval(rs.getBoolean("usa_FinalApproval"))
                        .submitted(rs.getBoolean("usa_Submitted"))
                        .modifiedOn(rs.getLong("usa_ModifiedOn"))
                        .createdBy(rs.getString("usa_CreatedBy"))
                        .modifiedBy(rs.getString("usa_ModifiedBy"))
                        .user(user)
                        .auditDetails(auditDetails)
                        .build();

                // Add the children properties to the SchemeApplication object
                addChildrenToProperty(rs, schemeApplication);

                // Add the SchemeApplication object to the map
                schemeApplicationMap.put(id, schemeApplication);
            }
        }
        // Return a list of SchemeApplication objects
        return new ArrayList<>(schemeApplicationMap.values());
    }

    // Method to add child properties to the SchemeApplication object
    private void addChildrenToProperty(ResultSet rs, SchemeApplication schemeApplication) throws SQLException {
        addAddressToApplication(rs, schemeApplication);
    }

    // Method to add address details to the SchemeApplication object
    private void addAddressToApplication(ResultSet rs, SchemeApplication schemeApplication) throws SQLException {
        // Sundeep : Need to populate or set all required fields from Scheme Application request into address object
        // This will be done by Basu.
        Address address = Address.builder()
                .id(rs.getLong("addr_id"))
                .userId(rs.getLong("addr_userid"))
                .tenantId(rs.getString("addr_tenantid"))
                //.address1(rs.getString("addr_Address1"))
                //.address2(rs.getString("addr_Address2"))
                //.location(rs.getString("addr_Location"))
                //.ward(rs.getString("addr_Ward"))
                .city(rs.getString("addr_City"))
                //.district(rs.getString("addr_District"))
                //.state(rs.getString("addr_State"))
                //.country(rs.getString("addr_Country"))
                //.pincode(rs.getString("addr_Pincode"))
                .build();

        schemeApplication.setAddress(address);
    }
}
