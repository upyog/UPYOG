package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;

import digit.repository.CommonSearchCriteria;

@Component
public class CommonQueryBuilder {
    // for Caste
    private static final String BASE_QUERY = """
            SELECT  \
            id , UPPER(name) as name \
            FROM \
            """;

    private static final String ORDERBY_NAME = " ORDER BY name DESC ";

    public String getSchemeSearchQuery(CommonSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        
        switch (criteria.getOption().toLowerCase()) {
            case "caste":
                query.append("eg_bmc_Caste as tbl ");
                break;
            case "religion":
                query.append("eg_bmc_Religion as tbl");
                break;
            case "qualification":
                query.append("eg_bmc_qualificationmaster as tbl");
                break;
            case "divyang":
                query.append("eg_bmc_divyang as tbl");
                break;
            case "document":
                query.append("eg_bmc_document as tbl");
                break;
            default:
                query.append("(Select 0 as id, 'No Record found'  as name) as tbl ");
                break;
        }
        query.append(ORDERBY_NAME);
        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

}
