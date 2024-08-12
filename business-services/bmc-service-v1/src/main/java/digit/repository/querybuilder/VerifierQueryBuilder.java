package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.web.models.SchemeApplicationSearchCriteria;

@Component
public class VerifierQueryBuilder {

    private static final String BASE_QUERY = """

                select eb.applicationnumber ,eb.userid ,eb.tenantid,
                bs.name as scheme
            """;

    private static final String FROM_TABLES = """
            FROM public.eg_bmc_userschemeapplication eb
            LEFT JOIN public.eg_bmc_aadharuser eba ON eb.userid = eba.userid AND eb.tenantid = eba.tenantid
            LEFT JOIN public.eg_bmc_schemes bs ON eb.optedid = bs.id
            LEFT JOIN public.eg_bmc_schememachine ebs ON eb.optedid = ebs.schemeid
            LEFT JOIN public.eg_bmc_machines ebm ON ebs.machineid = ebm.id
            LEFT JOIN public.eg_bmc_schemecourse ebs2 ON eb.optedid = ebs2.schemeid
            LEFT JOIN public.eg_bmc_courses bc ON ebs2.courseid = bc.id
            """;

    private static final String BASE_GROUP_BY = """
             GROUP BY eb.applicationnumber,eb.userid ,eb.tenantid,bs.name
            """;
    private static final String MACHINE_GROUP_BY = """
            ,machine
            """;

    private static final String COURSE_GROUP_BY = """
            ,course
            """;

    public String getVerificationSearchQuery(SchemeApplicationSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        if (ObjectUtils.isEmpty(criteria.getMachineId())) {
            query.append(", array_agg(ebm.name) as machine ");
        } else {
            query.append(", ebm.name as machine ");
        }
        if (ObjectUtils.isEmpty(criteria.getCourseId())) {
            query.append(", array_agg(bc.coursename) as course ");
        } else {
            query.append(" ,bc.coursename as course ");
        }
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(criteria.getCourseId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ebs2.courseId = ?");
            preparedStmtList.add(criteria.getCourseId());

        }
        if (!ObjectUtils.isEmpty(criteria.getMachineId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ebs.machineid = ? ");
            preparedStmtList.add(criteria.getMachineId());
        }
        if (!ObjectUtils.isEmpty(criteria.getSchemeId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" bs.id = ? ");
            preparedStmtList.add(criteria.getSchemeId());
        }
        addClauseIfRequired(query, preparedStmtList);
        // query.append("ud.available = true ");
        query.append(" eb.verificationstatus != true ");
        query.append(BASE_GROUP_BY);

        if (!ObjectUtils.isEmpty(criteria.getMachineId())) {
            query.append(MACHINE_GROUP_BY);
        }
        if (!ObjectUtils.isEmpty(criteria.getCourseId())) {
            query.append(COURSE_GROUP_BY);
        }

        return query.toString();
    }

    /**
     * Adds a clause to the query if required based on the state of the prepared
     * statement list.
     *
     * @param query            The query string builder.
     * @param preparedStmtList The list of parameters for the prepared statement.
     */
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

}
