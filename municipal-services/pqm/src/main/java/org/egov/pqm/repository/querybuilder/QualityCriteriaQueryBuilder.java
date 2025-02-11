package org.egov.pqm.repository.querybuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.pqm.util.QueryBuilderUtil.*;

@Component
public class QualityCriteriaQueryBuilder {

    private static final String QUALITY_CRITERIA_QUERY = "SELECT * FROM eg_pqm_test_criteria_results";

    public String getQueryString(List<String> idList, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(QUALITY_CRITERIA_QUERY);

        if (!CollectionUtils.isEmpty(idList)) {
            addToWhereClause(preparedStmtList, builder);
            builder.append(" testid IN (").append(addParamsToQuery(idList)).append(")");
            addToPreparedStatement(preparedStmtList, idList);
        }
        return builder.toString();
    }
}
