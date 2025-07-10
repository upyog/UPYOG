package org.upyog.rs.repository.rowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.upyog.rs.web.models.Address;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.AuditDetails;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * This is generic row mapper that will map columns of table to model classes by extracting from ResultSet
 * Column name   Model Attribute name      MappingStatus
 * <p>
 * createdBy      createdBy                     Yes
 * applicant_name  applicantName                Yes
 * mobileNUmber    mobileNo                     No
 *
 * @param <T>
 *
 *     TODO: Need to refactor it and Handle Applicant detail and Address Details
 */
@Slf4j
public class GenericRowMapper<T> implements ResultSetExtractor<List<T>> {

    private final Class<T> mappedClass;

    public GenericRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public List<T> extractData(ResultSet rs) {
        List<T> results = new ArrayList<>();

        try {
            // Get metadata for column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                T instance = mappedClass.getDeclaredConstructor().newInstance();

                // Map to hold column values
                Map<String, Object> columnValueMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i).toLowerCase(); // Column name in lowercase
                    Object columnValue = rs.getObject(i);
                    columnName = columnName.replace("_", "");
                    columnValueMap.put(columnName, columnValue);
                }

                // Map fields to column values
                for (Field field : mappedClass.getDeclaredFields()) {

                    String fieldName = field.getName().toLowerCase(); // Match field name to column name
                    if (columnValueMap.containsKey(fieldName)) {
                        field.setAccessible(true);
                        Object value = columnValueMap.get(fieldName);

                        // Handle LocalDate conversion
//                        if (field.getType().equals(LocalDate.class) && value instanceof java.sql.Date) {
//                            value = ((java.sql.Date) value).toLocalDate();
//                        }

                        value = convertValueToFieldType(field, value);

                        field.set(instance, value);
                    }


                }

                // Special handling for WaterTankerBookingDetail
                if (instance instanceof WaterTankerBookingDetail) {
                    WaterTankerBookingDetail bookingDetail = (WaterTankerBookingDetail) instance;

                    // Audit Details
                    AuditDetails auditDetails = extractAuditDetails(rs);
                    bookingDetail.setAuditDetails(auditDetails);
                    
                    /*
                     * Extract applicant and address details only when isUserProfileEnabled=false.
                     * When user profile is disabled, booking needs complete applicant and address info
                     * from request payload since user service integration is not available.
                     */
                    // Extract applicant and address details if available
                    ApplicantDetail applicantDetail = extractApplicantDetails(rs);
                    if (applicantDetail != null) {
                        bookingDetail.setApplicantDetail(applicantDetail);
                        bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
                    }
                    
                    Address address = extractAddressDetails(rs);
                    if (address != null) {
                        bookingDetail.setAddress(address);
                    }
                }
                
                if (instance instanceof MobileToiletBookingDetail) {
                    MobileToiletBookingDetail bookingDetail = (MobileToiletBookingDetail) instance;
                    // Audit Details
                    AuditDetails auditDetails = extractAuditDetails(rs);
                    bookingDetail.setAuditDetails(auditDetails);
                    
                    /*
                     * Extract applicant and address details only when isUserProfileEnabled=false.
                     * When user profile is disabled, booking needs complete applicant and address info
                     * from request payload since user service integration is not available.
                     */
                    // Extract applicant and address details if available
                    ApplicantDetail applicantDetail = extractApplicantDetails(rs);
                    if (applicantDetail != null) {
                        bookingDetail.setApplicantDetail(applicantDetail);
                        bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
                    }
                    
                    Address address = extractAddressDetails(rs);
                    if (address != null) {
                        bookingDetail.setAddress(address);
                    }
                }
                results.add(instance);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract data to class: " + mappedClass.getName(), e);
        }

        return results;
    }

    private Object convertValueToFieldType(Field field, Object value) {
        // Handle null values
        if (value == null) {
            // Return null for nullable fields
            return null;
        }

        Class<?> fieldType = field.getType();

        // Handle LocalDate conversion
        if (fieldType.equals(LocalDate.class) && value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }

        // Handle LocalTime conversion
        if (fieldType.equals(LocalTime.class)) {
            if (value instanceof Time) {
                return ((Time) value).toLocalTime();
            } else if (value instanceof String) {
                try {
                    return LocalTime.parse((String) value);
                } catch (Exception e) {
                    log.warn("Could not parse LocalTime from string: {}", value);
                    return null;
                }
            }
        }

        // Default case: return original value
        return value;
    }

    private AuditDetails extractAuditDetails(ResultSet rs) throws SQLException {
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("createdby"));
        auditDetails.setCreatedTime(rs.getLong("createdtime"));
        auditDetails.setLastModifiedBy(rs.getString("lastmodifiedby"));
        auditDetails.setLastModifiedTime(rs.getLong("lastmodifiedtime"));
        return auditDetails;
    }
    
    /**
     * Extracts applicant details from the ResultSet.
     * Returns null if no applicant details are available.
     *
     * @param rs ResultSet containing applicant details
     * @return ApplicantDetail object or null if not available
     */
    private ApplicantDetail extractApplicantDetails(ResultSet rs) throws SQLException {
        try {
            String applicantId = rs.getString("applicant_id");
            if (applicantId == null) {
                return null; // No applicant details available
            }
            
            ApplicantDetail applicantDetail = new ApplicantDetail();
            applicantDetail.setApplicantId(applicantId);
            applicantDetail.setName(rs.getString("name"));
            applicantDetail.setMobileNumber(rs.getString("mobile_number"));
            applicantDetail.setEmailId(rs.getString("email_id"));
            applicantDetail.setBookingId(rs.getString("booking_id"));
            applicantDetail.setAlternateNumber(rs.getString("alternate_number"));
            return applicantDetail;
        } catch (SQLException e) {
            // Column not found, return null
            return null;
        }
    }
    
    /**
     * Extracts address details from the ResultSet.
     * Returns null if no address details are available.
     *
     * @param rs ResultSet containing address details
     * @return Address object or null if not available
     */
    private Address extractAddressDetails(ResultSet rs) throws SQLException {
        try {
            Address address = new Address();
            address.setApplicantId(rs.getString("applicant_id"));
            address.setHouseNo(rs.getString("house_no"));
            address.setAddressLine1(rs.getString("address_line_1"));
            address.setAddressLine2(rs.getString("address_line_2"));
            address.setStreetName(rs.getString("street_name"));
            address.setLandmark(rs.getString("landmark"));
            address.setCity(rs.getString("city"));
            address.setCityCode(rs.getString("city_code"));
            address.setLocality(rs.getString("locality"));
            address.setLocalityCode(rs.getString("locality_code"));
            address.setPincode(rs.getString("pincode"));
            return address;
        } catch (SQLException e) {
            // Column not found, return null
            return null;
        }
    }



}
