package org.upyog.cdwm.repository.rowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.upyog.cdwm.web.models.CNDApplicationDetail;

import digit.models.coremodels.AuditDetails;
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
                if (instance instanceof CNDApplicationDetail) {
                    CNDApplicationDetail applicationDetail = (CNDApplicationDetail) instance;

                    // Applicant Details
                   /* ApplicantDetail applicantDetail = new ApplicantDetail();
                    applicantDetail.setName(rs.getString("name"));
                    applicantDetail.setMobileNumber(rs.getString("mobile_number"));
                    applicantDetail.setGender(rs.getString("gender"));
                    applicantDetail.setEmailId(rs.getString("email_id"));
                    applicantDetail.setAlternateNumber(rs.getString("alternate_number"));
                    bookingDetail.setApplicantDetail(applicantDetail);

                    // Address Details
                    Address address = new Address();
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
                    bookingDetail.setAddress(address);
                    
                    */

                    // Audit Details
                    AuditDetails auditDetails = new AuditDetails();
                    auditDetails.setCreatedBy(rs.getString("created_by"));
                    auditDetails.setCreatedTime(rs.getLong("created_time"));
                    auditDetails.setLastModifiedBy(rs.getString("last_modified_by"));
                    auditDetails.setLastModifiedTime(rs.getLong("last_modified_time"));
                    applicationDetail.setAuditDetails(auditDetails);
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
        
        // Handle Enum conversion
        if (fieldType.isEnum() && value instanceof String) {
            try {
                return Enum.valueOf((Class<Enum>) fieldType, ((String) value).toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid enum value: {} for field: {}", value, field.getName());
                return null;
            }
        }


        // Default case: return original value
        return value;
    }


}
