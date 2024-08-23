package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.web.models.SchemeApplicationSearchCriteria;

@Component
public class EmployeeGetApplicationQueryBuilder {


     private static final String BASE_QUERY = """
        SELECT  a.applicationnumber ,a.userid ,a.tenantid,a.agreetopay,a.statement,
         bs.name as scheme,
        ROW_NUMBER() OVER (PARTITION BY e.ward, optedid ORDER BY RANDOM()) AS rn
    """;

    private static final String FROM_TABLES = """
        FROM eg_bmc_userschemeapplication a
        LEFT JOIN eg_bmc_usersubschememapping f ON f.applicationnumber = a.applicationnumber 
        LEFT JOIN eg_bmc_schemes bs ON a.optedid = bs.id
        LEFT JOIN eg_bmc_userotherdetails d ON a.userid = d.userid AND a.tenantid = d.tenantid
        LEFT JOIN eg_bmc_employeewardmapper e ON e.uuid = ? AND e.ward = d.ward
        LEFT JOIN (
            SELECT h.businessid
            FROM eg_wf_processinstance_v2 h
            INNER JOIN eg_wf_state_v2 b ON h.status = b.uuid
            WHERE action = ? AND previousstatus = ?
        ) AS b ON b.businessid = a.applicationnumber
    """;

    private static final String RANKED_QUERY = """
        WITH RankedData AS (
            %s %s
    """;
    private static final String RANKED_QUERY_SELECT = """
            )
        SELECT *,
        CASE WHEN rn <= 1 THEN 'Selected' ELSE 'NotSelected' END
        FROM RankedData
        WHERE rn <= ?
            """;

    public String getQueryBasedOnAction(List<Object> preparedStmtList, SchemeApplicationSearchCriteria criteria) {
        StringBuilder query = new StringBuilder();


        if (!ObjectUtils.isEmpty(criteria.getState())) {

            if ("randomize".equalsIgnoreCase(criteria.getState())) {

                query.append(String.format(RANKED_QUERY, BASE_QUERY, FROM_TABLES));
            }
            else{
                query.append(BASE_QUERY)
                    .append(FROM_TABLES); 
            }
        }
        if (!ObjectUtils.isEmpty(criteria.getUuid())) {
            preparedStmtList.add(criteria.getUuid());
        }
        if (!ObjectUtils.isEmpty(criteria.getState())) {
            preparedStmtList.add(criteria.getState().toUpperCase());
        }
        preparedStmtList.add(criteria.getPreviousState());
        if (!ObjectUtils.isEmpty(criteria.getSchemeId())) {
            query.append("where bs.id = ? ");
            preparedStmtList.add(criteria.getSchemeId());
        }
        if (!ObjectUtils.isEmpty(criteria.getMachineId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" f.machineid = ? ");
            preparedStmtList.add(criteria.getMachineId());
        }
        if (!ObjectUtils.isEmpty(criteria.getCourseId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" f.courseId = ?");
            preparedStmtList.add(criteria.getCourseId());
        }

        if ("randomize".equalsIgnoreCase(criteria.getState())) {
            query.append(RANKED_QUERY_SELECT);
            preparedStmtList.add(criteria.getRandomizationNumber());
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


