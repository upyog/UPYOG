package org.upyog.tp.repository.rowMapper; // NOSONAR java:S120

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

import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.tp.web.models.Address;
import org.upyog.tp.web.models.ApplicantDetail;
import org.upyog.tp.web.models.AuditDetails;
import org.upyog.tp.web.models.DocumentDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;

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
 */
@Slf4j
@SuppressWarnings({"java:S3437", "java:S2143", "java:S6212", "java:S6213", "java:S2638", "java:S3011", "java:S3776", "java:S120"})
public class GenericRowMapper<T> implements ResultSetExtractor<List<T>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<T> mappedClass;

    public GenericRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public List<T> extractData(ResultSet tp) throws SQLException, DataAccessException {
        List<T> results = new ArrayList<>();

        try {
            ResultSetMetaData metaData = tp.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (tp.next()) {
                results.add(mapRow(tp, metaData, columnCount));
            }

        } catch (ReflectiveOperationException e) {
            throw new CustomException("ROW_MAPPING_ERROR",
                    "Failed to extract data to class: " + mappedClass.getName());
        }

        return results;
    }

    private T mapRow(ResultSet tp, ResultSetMetaData metaData, int columnCount) throws ReflectiveOperationException, SQLException {
        T instance = mappedClass.getDeclaredConstructor().newInstance();
        Map<String, Object> columnValueMap = buildColumnValueMap(tp, metaData, columnCount);
        mapFieldsToInstance(instance, columnValueMap);

        if (instance instanceof TreePruningBookingDetail bookingDetail) {
            enrichTreePruningBookingDetail(tp, bookingDetail);
        }
        return instance;
    }

    private Map<String, Object> buildColumnValueMap(ResultSet tp, ResultSetMetaData metaData, int columnCount)
            throws SQLException {
        Map<String, Object> columnValueMap = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i).toLowerCase().replace("_", "");
            columnValueMap.put(columnName, tp.getObject(i));
        }
        return columnValueMap;
    }

    private void mapFieldsToInstance(T instance, Map<String, Object> columnValueMap)
            throws IllegalAccessException {
        for (Field field : mappedClass.getDeclaredFields()) {
            String fieldName = field.getName().toLowerCase();
            if (!columnValueMap.containsKey(fieldName)) {
                continue;
            }
            field.setAccessible(true);
            Object value = convertValueToFieldType(field, columnValueMap.get(fieldName));
            field.set(instance, value);
        }
    }

    private void enrichTreePruningBookingDetail(ResultSet tp, TreePruningBookingDetail bookingDetail) throws SQLException {
        AuditDetails auditDetails = extractAuditDetails(tp);
        bookingDetail.setAuditDetails(auditDetails);

        DocumentDetail documentDetail = extractDocumentDetails(tp, bookingDetail);
        if (documentDetail != null) {
            List<DocumentDetail> documentDetails = new ArrayList<>();
            documentDetails.add(documentDetail);
            bookingDetail.setDocumentDetails(documentDetails);
        }

        ApplicantDetail applicantDetail = extractApplicantDetails(tp);
        if (applicantDetail != null) {
            bookingDetail.setApplicantDetail(applicantDetail);
            bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
        }
        Address address = extractAddressDetails(tp);
        if (address != null) {
            bookingDetail.setAddress(address);
        }
    }

    private Object convertValueToFieldType(Field field, Object value) {
        if (value == null) {
            return null;
        }

        // Handle JSONB / JSON columns (returned as PGobject) -> parse to JsonNode so they
        // serialize back as proper JSON instead of a raw PGobject wrapper.
        if (value instanceof PGobject pgObject) {
            String json = pgObject.getValue();
            if (json == null) {
                return null;
            }
            try {
                return OBJECT_MAPPER.readTree(json);
            } catch (Exception e) {
                log.warn("Could not parse JSON from column value: {}", json);
                return null;
            }
        }

        Class<?> fieldType = field.getType();

        if (fieldType.equals(LocalDate.class) && value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }

        if (fieldType.equals(LocalTime.class)) {
            if (value instanceof Time time) {
                return time.toLocalTime();
            }
            if (value instanceof String string) {
                try {
                    return LocalTime.parse(string);
                } catch (Exception e) {
                    log.warn("Could not parse LocalTime from string: {}", value);
                    return null;
                }
            }
        }

        return value;
    }

    private AuditDetails extractAuditDetails(ResultSet tp) throws SQLException {
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(tp.getString("createdby"));
        auditDetails.setCreatedTime(tp.getLong("createdtime"));
        auditDetails.setLastModifiedBy(tp.getString("lastmodifiedby"));
        auditDetails.setLastModifiedTime(tp.getLong("lastmodifiedtime"));
        return auditDetails;
    }

    private DocumentDetail extractDocumentDetails(ResultSet tp, TreePruningBookingDetail bookingDetail) throws SQLException {
        String documentDetailId = tp.getString("document_detail_id");
        if (documentDetailId == null) {
            return null;
        }

        return DocumentDetail.builder()
                .documentDetailId(documentDetailId)
                .bookingId(tp.getString("booking_id"))
                .documentType(tp.getString("document_type"))
                .fileStoreId(tp.getString("filestore_id"))
                .auditDetails(bookingDetail.getAuditDetails())
                .build();
    }


    /**
     * Extracts applicant details from the ResultSet.
     * Returns null if no applicant details are available.
     *
     * @param tp ResultSet containing applicant details
     * @return ApplicantDetail object or null if not available
     */
    private ApplicantDetail extractApplicantDetails(ResultSet tp) throws SQLException {
        try {
            String applicantId = tp.getString("applicant_id");
            if (applicantId == null) {
                return null;
            }

            ApplicantDetail applicantDetail = new ApplicantDetail();
            applicantDetail.setApplicantId(applicantId);
            applicantDetail.setName(tp.getString("name"));
            applicantDetail.setBookingId(tp.getString("booking_id"));
            applicantDetail.setMobileNumber(tp.getString("mobile_number"));
            applicantDetail.setEmailId(tp.getString("email_id"));
            applicantDetail.setAlternateNumber(tp.getString("alternate_number"));
            return applicantDetail;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Extracts address details from the ResultSet.
     * Returns null if no address details are available.
     *
     * @param tp ResultSet containing address details
     * @return Address object or null if not available
     */
    private Address extractAddressDetails(ResultSet tp) throws SQLException {
        try {
            Address address = new Address();
            address.setApplicantId(tp.getString("applicant_id"));
            address.setHouseNo(tp.getString("house_no"));
            address.setAddressLine1(tp.getString("address_line_1"));
            address.setAddressLine2(tp.getString("address_line_2"));
            address.setStreetName(tp.getString("street_name"));
            address.setLandmark(tp.getString("landmark"));
            address.setCity(tp.getString("city"));
            address.setCityCode(tp.getString("city_code"));
            address.setLocality(tp.getString("locality"));
            address.setLocalityCode(tp.getString("locality_code"));
            address.setPincode(tp.getString("pincode"));
            return address;
        } catch (SQLException e) {
            return null;
        }
    }
}
