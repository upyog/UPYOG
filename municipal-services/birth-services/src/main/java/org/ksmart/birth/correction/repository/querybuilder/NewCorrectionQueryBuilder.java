package org.ksmart.birth.correction.repository.querybuilder;

import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class NewCorrectionQueryBuilder extends NewBaseCorrectionQuery {
    @Autowired
    CommonQueryBuilder commonQueryBuilder;
    private static final String QUERY = new StringBuilder().append("SELECT ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.timeofbirth as ba_timeofbirth,ebd.am_pm as ba_am_pm,ebd.firstname_en as ba_firstname_en,")
            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,")
            .append("ebd.tenantid as ba_tenantid,ebd.gender as ba_gender,ebd.remarks_en as ba_remarks_en,ebd.remarks_ml as ba_remarks_ml,ebd.aadharno as ba_aadharno,ebd.esign_user_code as ba_esign_user_code,")
            .append("ebd.esign_user_desig_code as ba_esign_user_desig_code,ebd.is_father_info_missing as ba_is_father_info_missing,ebd.is_mother_info_missing as ba_is_mother_info_missing,")
            .append("ebd.applicationtype as ba_applicationtype,ebd.businessservice as ba_businessservice,ebd.workflowcode as ba_workflowcode,ebd.fm_fileno as ba_fm_fileno,")
            .append("ebd.file_date as ba_file_date,ebd.file_status as ba_file_status,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,")
            .append("ebd.registration_date as ba_registration_date,ebd.action as ba_action,ebd.status as ba_status,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime,ebd.lastmodifiedby").toString();
    private static final String QUERY_CORRECTION = new StringBuilder().append(" cor.id as co_id, cor.birthdtlid as co_birthdtlid, cor.correction_field_name as co_correction_field_name," )
                                                                      .append("cor.condition_code as co_condition_code, cor.specific_condition_code as co_specific_condition_code").toString();
    private static final String QUERY_CORRECTION_CHILD = new StringBuilder().append("corchld.id as ch_id, corchld.birthdtlid as ch_birthdtlid, corchld.correction_field_name as ch_correction_field_name,")
                                                                            .append("corchld.register_table_name as ch_register_table_name, corchld.register_column_name as ch_register_column_name,")
                                                                            .append("corchld.new_value as ch_new_value, corchld.old_value as ch_old_value,corchld.correction_id as ch_correction_id, corchld.local_column as ch_local_column").toString();
    private static final String QUERY_CORRECTION_DOCS = new StringBuilder().append("doc.id as do_id, doc.birthdtlid as do_birthdtlid, doc.correction_field_name as do_correction_field_name, doc.document_type as do_document_type, doc.filestoreid as do_filestoreid, doc.active as do_active,doc.correction_id as do_correction_id").toString();
    private static final String QUERYCORRECTIONCONDITION = new StringBuilder().append(" LEFT JOIN eg_birth_correction cor ON cor.birthdtlid = ebd.id")
            .append(" LEFT JOIN eg_birth_correction_document doc ON doc.birthdtlid = ebd.id")
            .append(" LEFT JOIN eg_birth_correction_child corchld ON corchld.birthdtlid = ebd.id ").toString();

    public String getNewBirthApplicationSearchQuery(@NotNull SearchCriteria criteria, CorrectionRequest request,
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
        StringBuilder query = new StringBuilder(commonQueryBuilder.getQueryMain());
        query.append(",").append(commonQueryBuilder.getQueryPlaceOfEvent())
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
                .append(",")
                .append(commonQueryBuilder.getQueryCareTaker())
                .append(",")
                .append(QUERY_CORRECTION)
                .append(",")
                .append(QUERY_CORRECTION_CHILD)
                .append(",")
                .append(QUERY_CORRECTION_DOCS)
                .append(commonQueryBuilder.getQueryConditionCommon())
                .append(QUERYCORRECTIONCONDITION).toString();
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

    public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query,CorrectionRequest request, @NotNull List<Object> preparedStmtValues) {
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
