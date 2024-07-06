package digit.repository.querybuilder;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.repository.SchemeSearchCriteria;

@Component
public class SchemeDetailQueryBuilder {
    // Conditions

    private static final String BASE_QUERY = """
            Select \
            ev.name as eventName , \
            to_timestamp(ev.startdt)::date as startDate, \
            to_timestamp(coalesce(ev.enddt,4102444799))::date as endDate, \
            sch.id as SchemeID, \
            sch.name as SchemeName, \
            sch.description as SchemeDescription, \
            cri.id as criteriaID, \
            cri.criteriatype, \
            cri.criteriavalue, \
            cri.criteriacondition, \
            schgrp.name as schemeHead, \
            schgrp.description as schemeheadDesc \
            """;
    private static final String COURSE_QUERY = """
            , \
            cr.id as courseID, \
            cr.coursename as courseName, \
            cr.description as courseDesc, \
            cr.duration as courseDuration, \
            to_timestamp(cr.startdt)::date as courseStartDate, \
            to_timestamp(coalesce(cr.enddt,4102444799))::date as courseEndDate, \
            cr.url as courseURL, \
            cr.institute as courseInstitute, \
            cr.imgurl as courseImageURL, \
            cr.instituteaddress as instututeAddress, \
            cr.amount as courseAmount \
            """;
    private static final String MAC_QUERY = """
            , \
            mac.id as machID, \
            mac.name as machName, \
            mac.description as machDesc, \
            mac.amount as machAmount \
            """;

    // From clause with join between Event, EventScheme, and Scheme tables
    private static final String FROM_TABLES = """
            from eg_bmc_event ev \
            left join eg_bmc_schemeevent schev on schev.eventid = ev.id \
            left join eg_bmc_schemes sch on schev.schemeid= sch.id \
            left join eg_bmc_scheme_criteria schcri on schcri.schemeid = sch.id \
            left join eg_bmc_criteria cri on schcri.criteriaid = cri.id \
            left join eg_bmc_scheme_group schgrp on schgrp.id = sch."SchemeGroupID" \
            left join eg_bmc_schemecourse schcr on schcr.schemeid = sch.id \
            left join eg_bmc_courses cr on cr.id = schcr.courseid \
            left join eg_bmc_schememachine schmac on schmac.schemeid = sch.id \
            left join eg_bmc_machines mac on mac.id = schmac.machineid \
            """;
    private static final String ORDERBY_MODIFIEDTIME = " ORDER BY sch.id, cri.criteriatype DESC ";

    public String getSchemeSearchQuery(SchemeSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        query.append(COURSE_QUERY);
        query.append(MAC_QUERY);
        query.append(FROM_TABLES);

        // Add where clause for Status if it is not empty
        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            switch (criteria.getStatus()) {
                case PAST:
                    addClauseIfRequired(query, preparedStmtList);
                    query.append(" to_timestamp(ev.enddt)::date < Cast(? as date) ");
                    preparedStmtList.add(LocalDate.now());
                    break;
                case FUTURE:
                    addClauseIfRequired(query, preparedStmtList);
                    query.append(" to_timestamp(ev.startdt)::date > Cast(? as date) ");
                    preparedStmtList.add(LocalDate.now());
                    break;
                case PRESENT:
                    addClauseIfRequired(query, preparedStmtList);
                    query.append(
                            " to_timestamp(ev.startdt)::date <= Cast(? as date) and to_timestamp(coalesce(ev.enddt,4102444799))::date >= Cast(? as date) ");
                            preparedStmtList.add(LocalDate.now());
                            preparedStmtList.add(LocalDate.now());
                    break;
                default:
                    if (!ObjectUtils.isEmpty(criteria.getStartDate())) {
                        addClauseIfRequired(query, preparedStmtList);
                        query.append(" to_timestamp(ev.startdt)::date <= Cast(? as date) ");
                        preparedStmtList.add(criteria.getStartDate());
                    }
                    if (!ObjectUtils.isEmpty(criteria.getEndDate())) {
                        addClauseIfRequired(query, preparedStmtList);
                        query.append(" to_timestamp(coalesce(ev.enddt,4102444799))::date >= Cast(? as date) ");
                        preparedStmtList.add(criteria.getEndDate());
                    }
                    break;
            }
        }

        if (!ObjectUtils.isEmpty(criteria.getSchemehead())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" schgrp.name = ? ");
            preparedStmtList.add(criteria.getSchemehead());
        }
        if (!ObjectUtils.isEmpty(criteria.getSchemeheaddesc())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" schgrp.description = ? ");
            preparedStmtList.add(criteria.getSchemeheaddesc());
        }
        if (!ObjectUtils.isEmpty(criteria.getId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sch.id = ? ");
            preparedStmtList.add(criteria.getId());
        }

        query.append(ORDERBY_MODIFIEDTIME);
        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    /**
     * Adds the given list of IDs to the prepared statement list.
     *
     * @param preparedStmtList The list of parameters for the prepared statement.
     * @param ids              The list of IDs to be added.
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(preparedStmtList::add);
    }

}
