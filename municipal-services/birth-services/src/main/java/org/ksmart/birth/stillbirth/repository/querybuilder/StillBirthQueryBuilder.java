package org.ksmart.birth.stillbirth.repository.querybuilder;

import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class StillBirthQueryBuilder extends BaseStillBirthQuery {
    @Autowired
    CommonQueryBuilder commonQueryBuilder;

    private static final String QUERY = "";

    public String getNewBirthApplicationSearchQuery(@NotNull SearchCriteria criteria, StillBirthDetailRequest request,
                                                    @NotNull List<Object> preparedStmtValues, Boolean isCount) {
        StringBuilder query = prepareSearchQuery();
        prepareSearchCriteria(criteria, query, preparedStmtValues);
        prepareSearchCriteriaFromRequest(query,request,preparedStmtValues);
        prepareOrderBy(criteria, query, preparedStmtValues);
        return query.toString();
    }
    public String getApplicationSearchQueryForRegistry(@NotNull SearchCriteria criteria, @NotNull List<Object> preparedStmtValues) {
        StringBuilder query = prepareSearchQuery();
        prepareSearchCriteria(criteria, query, preparedStmtValues);
        prepareOrderBy(criteria, query, preparedStmtValues);
        return query.toString();
    }

    public StringBuilder prepareSearchQuery() {
        StringBuilder query = new StringBuilder(QUERY);
        query.append(commonQueryBuilder.getQueryMain())
                .append(",")
                .append(commonQueryBuilder.getQueryPlaceOfEvent())
                .append(",")
                .append(commonQueryBuilder.getQueryFaterInfo())
                .append(",")
                .append(commonQueryBuilder.getQueryMoterInfo())
                .append(",")
                .append(commonQueryBuilder.getQueryPresent())
                .append(",")
                .append(commonQueryBuilder.getQueryPermanant())
                .append(",")
                .append(commonQueryBuilder.getQueryStat())
                .append(",")
                .append(commonQueryBuilder.getQueryIntiator())
                .append(commonQueryBuilder.getQueryCondition()).toString();
        return query;
    }

    public StringBuilder prepareSearchCriteria(@NotNull SearchCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
        addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilters("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);
        addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
        addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
        addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
        addFilter("ebd.gender", criteria.getGender(), query, preparedStmtValues);
        addFilter("ebd.applicationtype", criteria.getApplicationType(), query, preparedStmtValues);
        addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
        addDateRangeFilter("ebd.dateofbirth",  criteria.getDateOfBirthFrom(), criteria.getDateOfBirthTo(),query, preparedStmtValues);
        addDateRangeFilter("ebd.fm_fileno",  criteria.getFromDateFile(), criteria.getToDateFile(), query, preparedStmtValues);
        return query;
    }
    public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query, StillBirthDetailRequest request, @NotNull List<Object> preparedStmtValues) {
        if(preparedStmtValues.size() == 0) {
            addFilter("ebd.createdby", request.getRequestInfo().getUserInfo().getUuid(), query, preparedStmtValues);
            addFilter("ebd.status", "INITIATED", query, preparedStmtValues);
        }
        return query;
    }
    public StringBuilder prepareOrderBy(@NotNull SearchCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
        StringBuilder orderBy = new StringBuilder();
        if (StringUtils.isEmpty(criteria.getSortBy()))
            addOrderByColumns("ebd.createdtime",null, orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.dateOfBirth)
            addOrderByColumns("ebd.dateofbirth",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.applicationNumber)
            addOrderByColumns("ebd.applicationno",criteria.getSortOrder(),orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.mother)
            addOrderByColumns("ebmi.firstname_en",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.gender)
            addOrderByColumns("ebd.gender",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.registrationNo)
            addOrderByColumns("ebd.registrationno",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.tenantId)
            addOrderByColumns("ebd.tenantid",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.hospitalId)
            addOrderByColumns("ebp.hospitalid",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.institutionId)
            addOrderByColumns("ebp.institution_id",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.wardCode)
            addOrderByColumns("ebp.ward_id",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == SearchCriteria.SortBy.applicationType)
            addOrderByColumns("ebd.applicationtype",criteria.getSortOrder(), orderBy);
        addOrderToQuery(orderBy, query);
        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
        return query;
    }


}
