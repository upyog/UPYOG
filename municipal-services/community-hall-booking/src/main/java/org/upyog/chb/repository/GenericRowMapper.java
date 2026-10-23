package org.upyog.chb.repository;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
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
 * - Uses JavaBeans property descriptors to map result set columns to object properties.
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
 *    - Uses ResultSetMetaData to retrieve column names and dynamically maps them to properties.
 * 
 * Usage:
 * - This class is used in the repository layer to map database query results to objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 * 
 * Limitations:
 * - Requires that property names in the class match the column names in the database.
 * - May require additional handling for complex data types or nested objects.
 */
@Slf4j
public class GenericRowMapper<T> implements ResultSetExtractor<List<T>> {

    private final Class<T> mappedClass;

    /**
     * @param mappedClass target type whose JavaBean properties are matched to result-set column labels
     */
    public GenericRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    /**
     * Maps every row in {@code rs} to an instance of {@code T} using JavaBean property descriptors.
     *
     * @param rs JDBC result set positioned before the first row
     * @return list of mapped objects, empty when the result set has no rows
     * @throws SQLException when reading the result set fails
     * @throws DataAccessException when reflection-based mapping fails
     */
    @Override
    @SuppressWarnings("java:S2638")
    public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<T> results = new ArrayList<>();

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            BeanInfo beanInfo = Introspector.getBeanInfo(mappedClass, Object.class);

            while (rs.next()) {
                T instance = mappedClass.getDeclaredConstructor().newInstance();

                Map<String, Object> columnValueMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i).toLowerCase();
                    log.info("column name  {}", columnName);
                    Object columnValue = rs.getObject(i);
                    log.info("column value {} ", columnValue);
                    columnName = columnName.replace("_", "");
                    columnValueMap.put(columnName, columnValue);
                }

                for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                    String propertyName = propertyDescriptor.getName().toLowerCase();
                    if (propertyDescriptor.getWriteMethod() != null
                            && columnValueMap.containsKey(propertyName)) {
                        Object value = columnValueMap.get(propertyName);

                        if (propertyDescriptor.getPropertyType().equals(LocalDate.class)
                                && value instanceof java.sql.Date sqlDate) {
                            value = sqlDate.toLocalDate();
                        }

                        propertyDescriptor.getWriteMethod().invoke(instance, value);
                    }
                }

                results.add(instance);
            }

        } catch (SQLException | ReflectiveOperationException | IntrospectionException e) {
            throw new DataRetrievalFailureException("Failed to extract data to class: " + mappedClass.getName(), e);
        }

        return results;
    }
}
