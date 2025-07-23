package org.egov.user.persistence.repository;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.BloodGroup;
import org.egov.user.domain.model.enums.Gender;
import org.egov.user.domain.model.enums.GuardianRelation;
import org.egov.user.domain.model.enums.UserType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class responsible for creating audit records for user service operations.
 * 
 * This repository handles audit trail creation for various user-related entities including:
 * - User information changes
 * - Address modifications
 * - User role assignments
 * 
 * All audit records include timestamp and user identifier information for tracking purposes.
 */
@Repository
public class AuditRepository {
	
	public static final String INSERT_AUDIT_DETAILS = "insert into eg_user_audit_table (id,uuid,tenantid,salutation,dob,locale,username,password,pwdexpirydate,mobilenumber,altcontactnumber,emailid,active,name,gender,pan,aadhaarnumber,"
            + "type,guardian,guardianrelation,signature,accountlocked,bloodgroup,photo,identificationmark,auditcreatedby,auditcreatedtime) values (:id,:uuid,:tenantid,:salutation,"
            + ":dob,:locale,:username,:password,:pwdexpirydate,:mobilenumber,:alternatemobilenumber,:emailid,:active,:name,:gender,:pan,:aadhaarnumber,:type,:guardian,:guardianrelation,:signature,"
            + ":accountlocked,:bloodgroup,:photo,:identificationmark,:auditcreatedby,:auditcreatedtime) ";

    /**
     * SQL query to insert audit records into the user address audit table.
     * Captures all address-related changes for audit trail purposes.
     */
    public static final String INSERT_ADDRESS_AUDIT_DETAILS = "insert into eg_user_address_audit (id, version, createddate, lastmodifieddate, createdby, lastmodifiedby, type, address, city, pincode, userid, tenantid, auditcreatedby, auditcreatedtime) values (:id, :version, :createddate, :lastmodifieddate, :createdby, :lastmodifiedby, :type, :address, :city, :pincode, :userid, :tenantid, :auditcreatedby, :auditcreatedtime) ";
    
    /**
     * SQL query to insert audit records into the user role v1 audit table.
     * Captures user role assignments and changes for audit trail purposes.
     */
    public static final String INSERT_USERROLE_V1_AUDIT_DETAILS = "insert into eg_userrole_v1_audit (role_code, role_tenantid, user_id, user_tenantid, lastmodifieddate, auditcreatedby, auditcreatedtime) values (:role_code, :role_tenantid, :user_id, :user_tenantid, :lastmodifieddate, :auditcreatedby, :auditcreatedtime) ";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public AuditRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }    

	public void auditUser(User oldUser, long userId, String uuid) {
			
		Map<String, Object> auditInputs = new HashMap<String, Object>();

    	
    	auditInputs.put("auditcreatedby", uuid);
    	auditInputs.put("auditcreatedtime", Instant.now().toEpochMilli());
    	
    	auditInputs.put("id", oldUser.getId());
        auditInputs.put("uuid", oldUser.getUuid());
    	
    	auditInputs.put("username", oldUser.getUsername());
        auditInputs.put("type", oldUser.getType().toString());
        auditInputs.put("tenantid", oldUser.getTenantId());
        auditInputs.put("aadhaarnumber", oldUser.getAadhaarNumber());

        auditInputs.put("accountlocked", oldUser.getAccountLocked());
        auditInputs.put("accountlockeddate", oldUser.getAccountLockedDate());
        

        auditInputs.put("active", oldUser.getActive());
        auditInputs.put("altcontactnumber", oldUser.getAltContactNumber());

        List<Enum> bloodGroupEnumValues = Arrays.asList(BloodGroup.values());
        if (oldUser.getBloodGroup() != null) {
            if (bloodGroupEnumValues.contains(oldUser.getBloodGroup()))
                auditInputs.put("bloodgroup", oldUser.getBloodGroup().toString());
            else
                auditInputs.put("bloodgroup", "");
        }
        else {
            auditInputs.put("bloodgroup", "");
        }

        
        auditInputs.put("dob", oldUser.getDob());
        
        auditInputs.put("emailid", oldUser.getEmailId());

        if (oldUser.getGender() != null) {
            if (Gender.FEMALE.toString().equals(oldUser.getGender().toString())) {
                auditInputs.put("gender", 1);
            } else if (Gender.MALE.toString().equals(oldUser.getGender().toString())) {
                auditInputs.put("gender", 2);
            } else if (Gender.OTHERS.toString().equals(oldUser.getGender().toString())) {
                auditInputs.put("gender", 3);
            } else if (Gender.TRANSGENDER.toString().equals(oldUser.getGender().toString())) {
                auditInputs.put("gender", 4); 
            } else {
                auditInputs.put("gender", 0);
            }
        } else {
            auditInputs.put("gender", 0);
        }
        auditInputs.put("guardian", oldUser.getGuardian());

        List<Enum> enumValues = Arrays.asList(GuardianRelation.values());
        if (oldUser.getGuardianRelation() != null) {
            if(enumValues.contains(oldUser.getGuardianRelation()))
                auditInputs.put("guardianrelation", oldUser.getGuardianRelation().toString());
            else {
                auditInputs.put("guardianrelation", "");
            }
            
        } else {
            auditInputs.put("guardianrelation", "");
        }
        auditInputs.put("identificationmark", oldUser.getIdentificationMark());
        auditInputs.put("locale", oldUser.getLocale());
        if (null != oldUser.getMobileNumber())
            auditInputs.put("mobilenumber", oldUser.getMobileNumber());

        auditInputs.put("name", oldUser.getName());
        auditInputs.put("pan", oldUser.getPan());

        if (!isEmpty(oldUser.getPassword()))
            auditInputs.put("password", oldUser.getPassword());
        else {
        	auditInputs.put("password", "");
        }
        

        if ( oldUser.getPhoto() != null && oldUser.getPhoto().contains("http")) {
            auditInputs.put("photo", oldUser.getPhoto());
        }
        else {
        	auditInputs.put("photo", "");
        }

        
        auditInputs.put("pwdexpirydate", oldUser.getPasswordExpiryDate());
        
        auditInputs.put("salutation", oldUser.getSalutation());
        auditInputs.put("signature", oldUser.getSignature());
        auditInputs.put("title", oldUser.getTitle());


        List<Enum> userTypeEnumValues = Arrays.asList(UserType.values());
        if (oldUser.getType() != null) {
            if (userTypeEnumValues.contains(oldUser.getType()))
                auditInputs.put("type", oldUser.getType().toString());
            else {
                auditInputs.put("type", "");
            }
        }
        else {
            auditInputs.put("type", "");
        }
        

        auditInputs.put("alternatemobilenumber", oldUser.getAlternateMobileNumber());

        
        
    	
        namedParameterJdbcTemplate.update(INSERT_AUDIT_DETAILS, auditInputs); 
    	
	}

    /**
     * Creates an audit record for user address changes.
     * 
     * This method captures address modifications by inserting a record into the 
     * eg_user_address_audit table with the current timestamp and user identifier.
     * 
     * @param address Map containing address data to be audited
     * @param uuid User identifier who performed the action (can be null)
     */
    public void auditAddress(Map<String, Object> address, String uuid) {
        Map<String, Object> auditInputs = new HashMap<>(address);
        auditInputs.put("auditcreatedby", uuid);
        auditInputs.put("auditcreatedtime", Instant.now().toEpochMilli());
        namedParameterJdbcTemplate.update(INSERT_ADDRESS_AUDIT_DETAILS, auditInputs);
    }

    /**
     * Creates an audit record for user role v1 changes.
     * 
     * This method captures user role assignments and modifications by inserting 
     * a record into the eg_userrole_v1_audit table with the current timestamp 
     * and user identifier.
     * 
     * @param userroleV1 Map containing user role v1 data to be audited
     * @param uuid User identifier who performed the action (can be null)
     */
    public void auditUserRoleV1(Map<String, Object> userroleV1, String uuid) {
        Map<String, Object> auditInputs = new HashMap<>(userroleV1);
        auditInputs.put("auditcreatedby", uuid);
        auditInputs.put("auditcreatedtime", Instant.now().toEpochMilli());
        namedParameterJdbcTemplate.update(INSERT_USERROLE_V1_AUDIT_DETAILS, auditInputs);
    }
}
