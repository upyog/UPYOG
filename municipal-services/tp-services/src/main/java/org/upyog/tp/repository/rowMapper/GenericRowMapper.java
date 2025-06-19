package org.upyog.tp.repository.rowMapper;

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
public class GenericRowMapper<T> implements ResultSetExtractor<List<T>> {

    private final Class<T> mappedClass;

    public GenericRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public List<T> extractData(ResultSet tp) {
        List<T> results = new ArrayList<>();

        try {
            // Get metadata for column names
            ResultSetMetaData metaData = tp.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (tp.next()) {
                T instance = mappedClass.getDeclaredConstructor().newInstance();

                // Map to hold column values
                Map<String, Object> columnValueMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i).toLowerCase(); // Column name in lowercase
                    Object columnValue = tp.getObject(i);
                    columnName = columnName.replace("_", "");
                    columnValueMap.put(columnName, columnValue);
                }

                // Map fields to column values
                for (Field field : mappedClass.getDeclaredFields()) {

                    String fieldName = field.getName().toLowerCase(); // Match field name to column name
                    if (columnValueMap.containsKey(fieldName)) {
                        field.setAccessible(true);
                        Object value = columnValueMap.get(fieldName);

                        value = convertValueToFieldType(field, value);

                        field.set(instance, value);
                    }


                }
                if (instance instanceof TreePruningBookingDetail) {
                    TreePruningBookingDetail bookingDetail = (TreePruningBookingDetail) instance;

                    // Audit Details
                    AuditDetails auditDetails = extractAuditDetails(tp);
                    bookingDetail.setAuditDetails(auditDetails);
                    // Set DocumentDetails
                    DocumentDetail documentDetail = extractDocumentDetails(tp, bookingDetail);
                    if (documentDetail != null) {
                        List<DocumentDetail> documentDetails = new ArrayList<>();
                        documentDetails.add(documentDetail);
                        bookingDetail.setDocumentDetails(documentDetails);
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
            return null; // No document found
        }

        // Build and return DocumentDetail with audit details
        return DocumentDetail.builder()
                .documentDetailId(documentDetailId)
                .bookingId(tp.getString("booking_id"))
                .documentType(tp.getString("document_type"))
                .fileStoreId(tp.getString("filestore_id"))
                .auditDetails(bookingDetail.getAuditDetails())
                .build();
    }




}
