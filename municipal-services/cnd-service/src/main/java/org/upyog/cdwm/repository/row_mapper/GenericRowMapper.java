package org.upyog.cdwm.repository.row_mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataRetrievalFailureException;
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
 * Note: Applicant detail and address detail mapping for CNDApplicationDetail is handled by
 * {@link CNDApplicationDetailRowmapper}; this mapper only sets audit details for that type.
 */
@Slf4j
@SuppressWarnings("java:S2638")
public class GenericRowMapper<T> implements ResultSetExtractor<List<T>> {

    private final Class<T> mappedClass;

    public GenericRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public List<T> extractData(ResultSet rs) {
        List<T> results = new ArrayList<>();

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                T instance = mappedClass.getDeclaredConstructor().newInstance();

                Map<String, Object> columnValueMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i).toLowerCase();
                    Object columnValue = readColumnValue(rs, i);
                    columnName = columnName.replace("_", "");
                    columnValueMap.put(columnName, columnValue);
                }

                for (Field field : mappedClass.getDeclaredFields()) {
                    String fieldName = field.getName().toLowerCase();
                    if (columnValueMap.containsKey(fieldName)) {
                        Object value = columnValueMap.get(fieldName);
                        value = convertValueToFieldType(field, value);
                        setFieldValue(field, instance, value);
                    }
                }

                if (instance instanceof CNDApplicationDetail applicationDetail) {
                    AuditDetails auditDetails = new AuditDetails();
                    auditDetails.setCreatedBy(rs.getString("created_by"));
                    auditDetails.setCreatedTime(rs.getLong("created_time"));
                    auditDetails.setLastModifiedBy(rs.getString("last_modified_by"));
                    auditDetails.setLastModifiedTime(rs.getLong("last_modified_time"));
                    applicationDetail.setAuditDetails(auditDetails);
                }
                results.add(instance);
            }

        } catch (ReflectiveOperationException | SQLException e) {
            throw new DataRetrievalFailureException("Failed to extract data to class: " + mappedClass.getName(), e);
        }

        return results;
    }

    private static Object readColumnValue(ResultSet rs, int columnIndex) throws SQLException {
        Object localDate = rs.getObject(columnIndex, LocalDate.class);
        if (localDate != null) {
            return localDate;
        }
        Object localTime = rs.getObject(columnIndex, LocalTime.class);
        if (localTime != null) {
            return localTime;
        }
        return rs.getObject(columnIndex);
    }

    @SuppressWarnings("java:S3011")
    private static void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        if (!field.trySetAccessible()) {
            throw new IllegalAccessException("Cannot access field: " + field.getName());
        }
        field.set(instance, value);
    }

    private Object convertValueToFieldType(Field field, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> fieldType = field.getType();

        if (fieldType.equals(LocalDate.class) && value instanceof LocalDate localDate) {
            return localDate;
        }

        if (fieldType.equals(LocalTime.class)) {
            if (value instanceof LocalTime localTime) {
                return localTime;
            } else if (value instanceof String stringValue) {
                try {
                    return LocalTime.parse(stringValue);
                } catch (DateTimeParseException e) {
                    log.warn("Could not parse LocalTime from string: {}", value);
                    return null;
                }
            }
        }

        if (fieldType.isEnum() && value instanceof String stringValue) {
            return convertToEnum(fieldType, stringValue, field.getName());
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> E convertToEnum(Class<?> fieldType, String stringValue, String fieldName) {
        try {
            return Enum.valueOf((Class<E>) fieldType, stringValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid enum value: {} for field: {}", stringValue, fieldName);
            return null;
        }
    }

}
