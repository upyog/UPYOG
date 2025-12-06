package org.egov.pqm.repository.querybuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.pqm.util.QueryBuilderUtil.*;

@Component
public class TestDocumentQueryBuilder {
    private static final String TEST_DOCUMENTS_SEARCH_QUERY = "SELECT * FROM eg_pqm_test_result_documents";

    public String getSearchQuery(List<String> idList, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(TEST_DOCUMENTS_SEARCH_QUERY);

        if (!CollectionUtils.isEmpty(idList)) {
            addToWhereClause(preparedStmtList, builder);
            builder.append(" testId IN (").append(addParamsToQuery(idList)).append(")");
            addToPreparedStatement(preparedStmtList, idList);
        }
        return builder.toString();
    }
}
