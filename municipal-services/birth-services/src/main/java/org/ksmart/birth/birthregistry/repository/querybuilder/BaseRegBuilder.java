package org.ksmart.birth.birthregistry.repository.querybuilder;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.web.model.SearchCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class BaseRegBuilder {
    @Value("${egov.bnd.default.limit}")
    private Integer defaultBndLimit;

    @Value("${egov.bnd.default.offset}")
    private Integer defaultOffset;
    void addDateRangeFilter(String column, Long startDate, Long endDate, StringBuilder query,
                            List<Object> paramValues) {

        if (startDate != null || endDate != null) {
            addWhereClause(paramValues, query);
            query.append(" (");

            if (startDate != null) {
                query.append(column)
                        .append(" >= ? ");
                paramValues.add(startDate);
            }

            if (endDate != null) {
                if (startDate != null) {
                    query.append(" AND ");
                }

                query.append(column)
                        .append(" <= ? ");
                paramValues.add(endDate);
            }

            query.append(") ");
        }
    }

    void addFilters(String column, List<String> ids, StringBuilder query, List<Object> paramValues) {
        if (CollectionUtils.isNotEmpty(ids)) {
            addWhereClause(paramValues, query);
            query.append(column)
                    .append(" IN (")
                    .append(getStatementParameters(ids.size()))
                    .append(") ");
            ids.forEach(paramValues::add);
        }
    }

    void addFilter(String column, String value, StringBuilder query, List<Object> paramValues) {
        if (StringUtils.isNotBlank(value)) {
            addWhereClause(paramValues, query);
            query.append(column)
                    .append("=? ");
            paramValues.add(value);
        }
    }

    void addLongFilter(String column, Long value, StringBuilder query, List<Object> paramValues) {
        if (value != null)  {
            addWhereClause(paramValues, query);
            query.append(column)
                    .append("=? ");
            paramValues.add(value);
        }
    }

    void addDateToLongFilter(String column, String value, StringBuilder query, List<Object> paramValues) {
        if (value != null) {
            String strDate = "2015-08-04";
            LocalDate valueLocal = LocalDate.parse(strDate);
            Instant instant = valueLocal.atStartOfDay(ZoneId.systemDefault()).toInstant();
            addWhereClause(paramValues, query);
            query.append(column)
                    .append("=? ");
            paramValues.add(instant.toEpochMilli());
        }
    }

    void addLikeFilter(final String column, final String value, final StringBuilder query, final List<Object> paramValues) {
        if (StringUtils.isNotBlank(value)) {
            addWhereClause(paramValues, query);
            query.append(column)
                    .append(" LIKE ? ");
            paramValues.add(value.trim().toLowerCase()+"%");
        }
    }

    void addWhereClause(List<Object> values, StringBuilder query) {
        if (CollectionUtils.isEmpty(values)) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String getStatementParameters(int count) {
        return Collections.nCopies(count, "(?)")
                .stream()
                .collect(Collectors.joining(", "));
    }

    void addOrderClause(StringBuilder orderBy) {
        if (orderBy.length() == 0) {
            orderBy.append("  ORDER BY ");
        } else {
            orderBy.append(" ");
        }
    }

    void addOrderByColumns(String column, RegisterBirthSearchCriteria.SortOrder valueSort, StringBuilder orderBy){
        addOrderClause(orderBy);
        if(!StringUtils.isEmpty(column)){
            addOrderClause(orderBy);
            orderBy.append(column);
            addAscDesc(valueSort, orderBy);
        }
    }
    void addOrderToQuery(StringBuilder orderBy, StringBuilder query){
        if (orderBy.length() > 0) {
            String orderByStr = orderBy.toString().trim();
            orderByStr = orderByStr.substring(0, orderByStr.length() - 1);
            query.append(orderByStr);
        }
    }

    void addAscDesc(RegisterBirthSearchCriteria.SortOrder valueSort, StringBuilder query){
        if(valueSort == null)
            query.append(" ASC, ");
        else if(valueSort == RegisterBirthSearchCriteria.SortOrder.ASC)
            query.append(" ASC, ");
        else
            query.append(" DESC, ");
    }

     void addLimitAndOffset(Integer offset, Integer limit, StringBuilder query, final List<Object> paramValues) {
        // prepare Offset
         if (offset == null) {
             query.append(" OFFSET ? ");
             paramValues.add(defaultOffset);
         } else {
             query.append(" OFFSET ? ");
             paramValues.add(offset);
         }
         // prepare limit
         if (limit == null) {
             query.append(" LIMIT ? ");
             paramValues.add(defaultBndLimit);
         } else{
             query.append(" LIMIT ? ");
             paramValues.add(limit);
         }
     }
}
