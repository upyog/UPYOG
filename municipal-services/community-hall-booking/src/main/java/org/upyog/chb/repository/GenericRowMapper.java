package org.upyog.chb.repository;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is a generic row mapper that maps the result set from the database
 * to a list of objects of a specified type.
 * 
 * Purpose:
 * - To provide a reusable and flexible mechanism for mapping database rows to objects.
 * - To dynamically map result set columns to fields in the specified class using reflection.
 * 
 * Features:
 * - Implements the ResultSetExtractor interface to process the ResultSet.
 * - Uses reflection to map result set columns to fields in the specified class.
 * - Handles various data types, including primitive types, strings, and dates.
 * - Logs errors and exceptions for debugging and monitoring purposes.
 * 
 * Dependencies:
 * - Lombok's @Slf4j: Used for logging errors and debugging information.
 * 
 * Constructor:
 * - Accepts a Class<T> parameter to specify the type of objects to map.
 * 
 * Methods:
 * 1. extractData:
 *    - Processes the ResultSet and maps each row to an object of the specified type.
 *    - Uses ResultSetMetaData to retrieve column names and dynamically maps them to fields.
 * 
 * Usage:
 * - This class is used in the repository layer to map database query results to objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 * 
 * Limitations:
 * - Requires that the field names in the class match the column names in the database.
 * - May require additional handling for complex data types or nested objects.
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
                    log.info("column name  {}", columnName);
                    Object columnValue = rs.getObject(i);
                    log.info("column value {} ", columnValue);
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
                        if (field.getType().equals(LocalDate.class) && value instanceof java.sql.Date) {
                            value = ((java.sql.Date) value).toLocalDate();
                        }

                        field.set(instance, value);
                    }
                }

                results.add(instance);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract data to class: " + mappedClass.getName(), e);
        }

        return results;
    }
}

