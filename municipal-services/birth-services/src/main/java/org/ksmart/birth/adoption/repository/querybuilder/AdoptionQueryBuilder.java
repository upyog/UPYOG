package org.ksmart.birth.adoption.repository.querybuilder;

import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class AdoptionQueryBuilder extends BaseAdoptionQuery {

	@Autowired
    CommonQueryBuilder commonQueryBuilder;
	private static final String QUERY = "";
//	 private static final String QUERY = new StringBuilder().append("SELECt ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.timeofbirth as ba_timeofbirth,ebd.am_pm as ba_am_pm,ebd.firstname_en as ba_firstname_en,")
//	            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,")
//	            .append("ebd.tenantid as ba_tenantid,ebd.gender as ba_gender,ebd.remarks_en as ba_remarks_en,ebd.remarks_ml as ba_remarks_ml,ebd.aadharno as ba_aadharno,ebd.esign_user_code as ba_esign_user_code,")
//	            .append("ebd.esign_user_desig_code as ba_esign_user_desig_code,ebd.is_father_info_missing as ba_is_father_info_missing,ebd.is_mother_info_missing as ba_is_mother_info_missing,")
//	            .append("ebd.applicationtype as ba_applicationtype,ebd.businessservice as ba_businessservice,ebd.workflowcode as ba_workflowcode,ebd.fm_fileno as ba_fm_fileno,")
//	            .append("ebd.file_date as ba_file_date,ebd.file_status as ba_file_status,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,")
//	            .append("ebd.registration_date as ba_registration_date,ebd.action as ba_action,ebd.status as ba_status,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime,ebd.lastmodifiedby").toString();
//
//	 private static final String QUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
//	            .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();

	 
//    public String getAdoptionSearchQuery(@NotNull SearchCriteria criteria,
//                                                 @NotNull List<Object> preparedStmtValues, Boolean isCount) {
//    	StringBuilder query = new StringBuilder(QUERY);
//        query.append(",").append(commonQueryBuilder.getQueryPlaceOfEvent())
//                .append(",")
//                .append(commonQueryBuilder.getQueryFaterInfo())
//                .append(",")
//                .append(commonQueryBuilder.getQueryMoterInfo())
//                .append(",")
//                .append(commonQueryBuilder.getQueryPresent())
//                .append(",")
//                .append(commonQueryBuilder.getQueryPermanant())
//                .append(",")
//                .append(commonQueryBuilder.getQueryStat())
////                .append(",")
////                .append(commonQueryBuilder.getQueryIntiator())
//                .append(QUERYCONDITION).toString();
//        
//        StringBuilder orderBy = new StringBuilder();
//        addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
//        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
//        addFilter("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
//        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
//        addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);
//        addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
//        addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
//        addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
//        addFilter("eebd.gender", criteria.getGender(), query, preparedStmtValues);
//        addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
//        addDateRangeFilter("ebd.dateofbirth",  criteria.getDateOfBirthFrom(), criteria.getDateOfBirthTo(),query, preparedStmtValues);
//        addDateRangeFilter("ebd.fm_fileno",  criteria.getFromDateFile(), criteria.getToDateFile(), query, preparedStmtValues);
//        
//
//        if (StringUtils.isEmpty(criteria.getSortBy()))
//            addOrderByColumns("ebd.createdtime",null, orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.dateOfBirth)
//            addOrderByColumns("ebd.dateofbirth",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.applicationNumber)
//            addOrderByColumns("ebd.applicationno",criteria.getSortOrder(),orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.mother)
//            addOrderByColumns("ebmi.firstname_en",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.gender)
//            addOrderByColumns("ebd.gender",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.registrationNo)
//            addOrderByColumns("ebd.registrationno",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.tenantId)
//            addOrderByColumns("ebd.tenantid",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.hospitalId)
//            addOrderByColumns("ebp.hospitalid",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.institutionId)
//            addOrderByColumns("ebp.institution_id",criteria.getSortOrder(), orderBy);
//        else if (criteria.getSortBy() == SearchCriteria.SortBy.wardCode)
//            addOrderByColumns("ebp.ward_id",criteria.getSortOrder(), orderBy);
//
//
//
//        addOrderToQuery(orderBy, query);
//        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
//        return query.toString();
//    }
	 
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
	                .append(commonQueryBuilder.getQueryDocuments())
	                .append(commonQueryBuilder.getQueryConditionAdptn()).toString();
	        return query;
	    }
	 public StringBuilder prepareRegistrySearchQuery() {
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
	                .append(commonQueryBuilder.getQueryConditionAdptnReg()).toString();
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

	    public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query,AdoptionDetailRequest request, @NotNull List<Object> preparedStmtValues) {
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
    public String getAdoptionSearchQuery(@NotNull SearchCriteria criteria, AdoptionDetailRequest request,
            @NotNull List<Object> preparedStmtValues, Boolean isCount) {
			StringBuilder query = prepareSearchQuery();
			prepareSearchCriteria(criteria, query, preparedStmtValues);
			prepareSearchCriteriaFromRequest(query,request,preparedStmtValues);
			prepareOrderBy(criteria, query, preparedStmtValues);
			return query.toString();
	}
    public String getApplicationSearchQueryForRegistry(@NotNull SearchCriteria criteria, @NotNull List<Object> preparedStmtValues) {
        StringBuilder query = prepareRegistrySearchQuery();
        prepareSearchCriteria(criteria, query, preparedStmtValues);
        prepareOrderBy(criteria, query, preparedStmtValues);
        return query.toString();
    }

}
