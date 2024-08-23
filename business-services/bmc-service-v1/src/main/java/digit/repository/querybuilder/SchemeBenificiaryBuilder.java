package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.repository.SchemeBeneficiarySearchCritaria;

@Component
public class SchemeBenificiaryBuilder {

    private static final String BASE_QUERY = """
            SELECT ua.optedid, ua.applicationnumber, ussm.createdon , sg.id, s.name \
            """;

    private static final String SCHEME_MACHINE_SELECT_QUERY = """
             , ussm.machineid \
            """;

    private static final String SCHEME_COURSE_SELECT_QUERY = """
            , ussm.courseid \
            """;

    // private static final String PENSION_QUERY = """
    // , COUNT(*) > 0 AS has_applied_for_pension \
    // """;

    private static final String FROM_TABLES = """
            FROM eg_bmc_userschemeapplication ua
            LEFT JOIN eg_bmc_usersubschememapping ussm ON ua.applicationnumber = ussm.applicationnumber
            LEFT JOIN eg_bmc_schemes s ON ua.optedid = s.id
            LEFT JOIN eg_bmc_scheme_group sg ON s."SchemeGroupID" = sg.id
            """;

    public String getSchemeDetailsSearchQuery(SchemeBeneficiarySearchCritaria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);

            query.append(SCHEME_MACHINE_SELECT_QUERY);

            query.append(SCHEME_COURSE_SELECT_QUERY);
        
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(criteria.getUserId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ua.userid = ? ");
            preparedStmtList.add(criteria.getUserId());
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ua.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        if (!ObjectUtils.isEmpty(criteria.getSubmitted())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ua.submitted = ? ");
            preparedStmtList.add(criteria.getSubmitted());
        }
        if (!ObjectUtils.isEmpty(criteria.getOptedId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ua.optedid = ? ");
            preparedStmtList.add(criteria.getOptedId());
        }

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
