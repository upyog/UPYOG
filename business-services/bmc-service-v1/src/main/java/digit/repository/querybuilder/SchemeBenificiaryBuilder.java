package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.repository.SchemeBeneficiarySearchCritaria;





@Component
public class SchemeBenificiaryBuilder {

    private static final String BASE_QUERY = """
        SELECT ua.optedid, e.startdt, e.enddt \
        """;

    private static final String SCHEME_MACHINE_SELECT_QUERY = """
         , sm.machineid \
        """;
        
    private static final String SCHEME_COURSE_SELECT_QUERY = """
         , sc.courseid \
         """; 

    private static final String PENSION_QUERY = """
            , COUNT(*) > 0 AS has_applied_for_pension  \
            """;     
                
    private static final String FROM_TABLES = """    
        FROM eg_bmc_userschemeapplication ua
        LEFT JOIN eg_bmc_schememachine sm ON ua.optedid = sm.schemeid
        LEFT JOIN eg_bmc_schemecourse sc ON ua.optedid = sc.schemeid
        LEFT JOIN eg_bmc_schemeevent se ON ua.optedid = se.schemeid
        LEFT JOIN eg_bmc_schemes s ON ua.optedid=s.id
        LEFT JOIN eg_bmc_event e ON se.eventid = e.id
        """;

     private static final String groupBy = """
             GROUP BY ua.optedid, e.startdt, e.enddt, sm.machineid, sc.courseid;
             """;   


public String getSchemeDetailsSearchQuery(SchemeBeneficiarySearchCritaria criteria, List<Object> preparedStmtList) {
    StringBuilder query = new StringBuilder(BASE_QUERY);
    if(Boolean.TRUE.equals(criteria.getForMachine())) {
        query.append(SCHEME_MACHINE_SELECT_QUERY);
    }
    if(Boolean.TRUE.equals(criteria.getForCourse())) {
        query.append(SCHEME_COURSE_SELECT_QUERY);
    }
    if(Boolean.TRUE.equals(criteria.getForPension())) {
        query.append(PENSION_QUERY);
    }
    query.append(FROM_TABLES);
    
    
    

    
    if (!ObjectUtils.isEmpty(criteria.getUserId())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" ua.userid = ? ");
        preparedStmtList.add(criteria.getUserId());
    }

    if (!ObjectUtils.isEmpty(criteria.getUserId())) {
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
        query.append(" se.schemeid = ? ");
        preparedStmtList.add(criteria.getOptedId());
    }
    if (!ObjectUtils.isEmpty(criteria.getName())) {
        addClauseIfRequired(query, preparedStmtList);
        query.append(" s.name = ? ");
        preparedStmtList.add(criteria.getName());
        query.append(groupBy);
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



