package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.repository.SchemeBeneficiarySearchCritaria;



@Component
public class SchemeBenificiaryBuilder {

    private static final String BASE_QUERY = """
        SELECT ua.optedid, e.startdt, e.enddt, \
        """;

    private static final String SCHEME_MACHINE_SELECT_QUERY = """
        SELECT sm.machineid, \
        """;
        
    private static final String SCHEME_COURSE_SELECT_QUERY = """
         SELECT sc.courseid, \
         """; 

    private static final String PENSION_QUERY = """
            SELECT COUNT(*) > 0 AS has_applied_for_pension , \
            """;     
                
    private static final String FROM_TABLES = """    
        FROM public.eg_bmc_userschemeapplication ua
        LEFT JOIN eg_bmc_schememachine sm ON ua.optedid = sm.schemeid
        LEFT JOIN eg_bmc_schemecourse sc ON ua.optedid = sc.schemeid
        LEFT JOIN eg_bmc_schemeevent se ON ua.optedid = se.schemeid
        LEFT JOIN eg_bmc_scheme s ON ua.optedid=s.id
        LEFT JOIN eg_bmc_event e ON se.eventid = e.id
        """;


public String getSchemeDetailsSearchQuery(SchemeBeneficiarySearchCritaria criteria, List<Object> preparedStmtList) {
    StringBuilder query = new StringBuilder(BASE_QUERY);
    if(criteria.getForMachine()==true) {
        query.append(SCHEME_MACHINE_SELECT_QUERY);
    }
    if(criteria.getForCourse()==true) {
        query.append(SCHEME_COURSE_SELECT_QUERY);
    }
    if(criteria.getForPension()==true) {
        query.append(PENSION_QUERY);
    }
    query.append(FROM_TABLES);

    
    if (!ObjectUtils.isEmpty(criteria.getUserId())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" ua.userid = ? ");
        preparedStmtList.add(criteria.getUserId());
    }

    
    if (!ObjectUtils.isEmpty(criteria.getSubmitted())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" ua.submitted = ? ");
        preparedStmtList.add(criteria.getSubmitted());
    }

    
    if (!ObjectUtils.isEmpty(criteria.getOptedId())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" se.schemeid = ? ");
        preparedStmtList.add(criteria.getOptedId());
    }
    if (!ObjectUtils.isEmpty(criteria.getName())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" s.name = ? ");
        preparedStmtList.add(criteria.getName());
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
