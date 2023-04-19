package org.ksmart.birth.birthnac.repository.querybuilder;

import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class NacQueryBuilder extends BaseNacQuery {

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
//	            .append(" LEFT JOIN eg_birth_applicant ebap ON ebap.birthdtlid = ebd.id")
//	            .append(" LEFT JOIN eg_birth_children_born ebcb ON ebcb.birthdtlid = ebd.id")
//	            .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();
//
//	 private static final String QUERYSTR = "";
//	 private static final String NACQUERY = new StringBuilder().append("SELECt ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.firstname_en as ba_firstname_en,")
//	            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,ebd.createdtime,")
//	            .append("ebd.tenantid as ba_tenantid,ebd.applicationtype as ba_applicationtype,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,ebd.registration_date as ba_registration_date,ebd.status as ba_status") .toString();
//	           
//	             
//	            
//	            
//
//	 private static final String NACQUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id ")
//	            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'") 
//	            .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
////	            .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
//	            .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();
//
//	 
//	 private static final String NAC_QUERY_PERMANANT_ADDRESS = new StringBuilder().append("eperad.id as per_id,eperad.housename_no_en as per_housename_no_en,")
//				.append("eperad.housename_no_ml as per_housename_no_ml,eperad.ot_address1_en as per_ot_address1_en,eperad.ot_address1_ml as per_ot_address1_ml,eperad.ot_address2_en as per_ot_address2_en,")
//				.append("eperad.ot_address2_ml as per_ot_address2_ml,eperad.ot_state_region_province_en as per_ot_state_region_province_en,eperad.ot_state_region_province_ml as per_ot_state_region_province_ml,")
//				.append("eperad.ot_zipcode as per_ot_zipcode,eperad.villageid as per_villageid,eperad.village_name as per_village_name,eperad.tenantid as per_tenantid,eperad.talukid as per_talukid,")
//				.append("eperad.taluk_name as per_taluk_name,eperad.ward_code as per_ward_code,eperad.locality_en as per_locality_en,")
//				.append("eperad.locality_ml as per_locality_ml,eperad.street_name_en as per_street_name_en,eperad.street_name_ml as per_street_name_ml,eperad.districtid as per_districtid,")
//				.append("eperad.stateid as per_stateid,eperad.poid as per_poid,eperad.pinno as per_pinno,eperad.countryid as per_countryid,eperad.birthdtlid as per_birthdtlid,")
//				.append("eperad.bio_adopt as per_bio_adopt,eperad.same_as_present as per_same_as_present,eperad.family_emailid as per_family_emailid,eperad.family_mobileno as per_family_mobileno,")
//				.append("eperad.postoffice_en as per_postoffice_en,eperad.postoffice_ml as per_postoffice_ml").toString();
//
//
//		private static final String NAC_QUERY_PRESENT_ADDRESS = new StringBuilder().append("epreadd.id as pres_id,epreadd.housename_no_en as pres_housename_no_en,")
//				.append("epreadd.housename_no_ml as pres_housename_no_ml,epreadd.ot_address1_en as pres_ot_address1_en,epreadd.ot_address1_ml as pres_ot_address1_ml,epreadd.ot_address2_en as pres_ot_address2_en,")
//				.append("epreadd.ot_address2_ml as pres_ot_address2_ml,epreadd.ot_state_region_province_en as pres_ot_state_region_province_en,epreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,")
//				.append("epreadd.ot_zipcode as pres_ot_zipcode,epreadd.villageid as pres_villageid,epreadd.village_name as pres_village_name,epreadd.tenantid as pres_tenantid,epreadd.talukid as pres_talukid,")
//				.append("epreadd.taluk_name as pres_taluk_name,epreadd.ward_code as pres_ward_code,epreadd.locality_en as pres_locality_en,")
//				.append("epreadd.locality_ml as pres_locality_ml,epreadd.street_name_en as pres_street_name_en,epreadd.street_name_ml as pres_street_name_ml,epreadd.districtid as pres_districtid,")
//				.append("epreadd.stateid as pres_stateid,epreadd.poid as pres_poid,epreadd.pinno as pres_pinno,epreadd.countryid as pres_countryid,epreadd.birthdtlid as pres_birthdtlid,")
//				.append("epreadd.bio_adopt as pres_bio_adopt,epreadd.postoffice_en as pres_postoffice_en,epreadd.postoffice_ml as pres_postoffice_ml").toString();
//
//
//		
//		
//
//	 private static final String NAC_QUERY_PLACE_OF_EVENT = new StringBuilder().append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.placeofbirthid as pla_placeofbirthid,ebp.hospitalid as pla_hospitalid,ebp.public_place_id as pla_public_place_id,ebp.institution_id as pla_institution_id,eperad.locality_en as eperad_locality_en,eperad.talukid as eperad_talukid,eperad.villageid as eperad_villageid,eperad.stateid as eperad_stateid").toString();
//	
//	 private static final String NAC_QUERY_MOTER_INFO = new StringBuilder().append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,") .toString();
//	
//	 private static final String QUERY_NACAPPLICANT = new StringBuilder().append("ebap.id as ebap_id,ebap.birthdtlid as ebap_birthdtlid,ebap.name_en as ebap_name_en,ebap.address_en as ebap_address_en,")
//				.append("ebap.aadharno as ebap_aadharno,ebap.mobileno as ebap_mobileno,ebap.is_declared as ebap_is_declared,")
//				.append("ebap.declaration_id as ebap_declaration_id,ebap.is_esigned as ebap_is_esigned").toString();
//
//
//	 public String getQueryMain() {
//			return NACQUERY;
//		}
//		public String getQueryCondition() {
//			return NACQUERYCONDITION;
//		}
//		public String getQueryPlaceOfEvent() {
//			return NAC_QUERY_PLACE_OF_EVENT;
//		}
//		 
//
//		public String getQueryMoterInfo() {
//			return NAC_QUERY_MOTER_INFO;
//		}
//				
//		public String getQueryNacApplicant() {
//			return QUERY_NACAPPLICANT;
//		}		
//		
	
	public String getNacSearchQuery(@NotNull SearchCriteria criteria, NacDetailRequest request,
            @NotNull List<Object> preparedStmtValues, Boolean isCount) {
			StringBuilder query = prepareSearchQuery();
			prepareSearchCriteria(criteria, query, preparedStmtValues);
			prepareSearchCriteriaFromRequest(query,request,preparedStmtValues);
			prepareOrderBy(criteria, query, preparedStmtValues);
			return query.toString();
	}
	
	
//		  public String getNacSearchQuery(@NotNull SearchCriteria criteria,
//                  @NotNull List<Object> preparedStmtValues, Boolean isCount) {
//				StringBuilder query = new StringBuilder(QUERY);
//			    query.append(commonQueryBuilder.getQueryMain())
//	            .append(",")
//				.append(commonQueryBuilder.getQueryPlaceOfEvent())
//				.append(",")
//				.append(commonQueryBuilder.getQueryFaterInfo())
//				.append(",")
//				.append(commonQueryBuilder.getQueryMoterInfo())
//				.append(",")
//				.append(commonQueryBuilder.getQueryPresent())
//				.append(",")
//				.append(commonQueryBuilder.getQueryPermanant())
//				.append(",")
//				.append(commonQueryBuilder.getQueryStat())
//				.append(",")
//				.append(commonQueryBuilder.getQueryIntiator())
//				.append(",")
//				.append(commonQueryBuilder.getQueryNacApplicant())
//				.append(",")
//				.append(commonQueryBuilder.getQueryOtherChildren())
//			    .append(commonQueryBuilder.getQueryCondition()).toString();
////				.append(QUERYCONDITION).toString();
//				
//				StringBuilder orderBy = new StringBuilder();
//				addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
//				addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
//
//			  if(criteria.getApplicationNumber().contains(",")) {
//				  addFilters("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
//			  } else{
//				  String appNo = criteria.getApplicationNumber().get(0);
//				  addFilter("ebd.applicationno", appNo, query, preparedStmtValues);
//			  }
//				addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
//				addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);
//				addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
//				addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
//				addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
//				addFilter("eebd.gender", criteria.getGender(), query, preparedStmtValues);
//				addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
//				addDateRangeFilter("ebd.dateofbirth",  criteria.getDateOfBirthFrom(), criteria.getDateOfBirthTo(),query, preparedStmtValues);
//				addDateRangeFilter("ebd.fm_fileno",  criteria.getFromDateFile(), criteria.getToDateFile(), query, preparedStmtValues);
//				
//				
//				if (StringUtils.isEmpty(criteria.getSortBy()))
//				addOrderByColumns("ebd.createdtime",null, orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.dateOfBirth)
//				addOrderByColumns("ebd.dateofbirth",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.applicationNumber)
//				addOrderByColumns("ebd.applicationno",criteria.getSortOrder(),orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.mother)
//				addOrderByColumns("ebmi.firstname_en",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.gender)
//				addOrderByColumns("ebd.gender",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.registrationNo)
//				addOrderByColumns("ebd.registrationno",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.tenantId)
//				addOrderByColumns("ebd.tenantid",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.hospitalId)
//				addOrderByColumns("ebp.hospitalid",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.institutionId)
//				addOrderByColumns("ebp.institution_id",criteria.getSortOrder(), orderBy);
//				else if (criteria.getSortBy() == SearchCriteria.SortBy.wardCode)
//				addOrderByColumns("ebp.ward_id",criteria.getSortOrder(), orderBy);
//				
//				
//				
//				addOrderToQuery(orderBy, query);
//				addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
//				return query.toString();
//				}
//					 
 
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
                .append(",")
                .append(commonQueryBuilder.getQueryNacApplicant())
				.append(",")
				.append(commonQueryBuilder.getQueryOtherChildren())		
				.append(",")
				.append(commonQueryBuilder.getQueryDocuments())
                .append(commonQueryBuilder.getQueryConditionNac()).toString();
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

	    public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query,NacDetailRequest request, @NotNull List<Object> preparedStmtValues) {
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
	    
//    public String getNacCertSearchQuery(@NotNull NacSearchCriteria criteria,
//            @NotNull List<Object> preparedStmtValues, Boolean isCount) {
//			StringBuilder query = new StringBuilder(CERTQUERY);
//			query.append(",").append(commonQueryBuilder.getQueryPlaceOfEvent())
//			.append(",")
//			.append(commonQueryBuilder.getQueryFaterInfo())
//			.append(",")
//			.append(commonQueryBuilder.getQueryMoterInfo())
//			.append(",")
//			.append(commonQueryBuilder.getQueryPresent())
//			.append(",")
//			.append(commonQueryBuilder.getQueryPermanant())
//			.append(",")
//			.append(commonQueryBuilder.getQueryStat())
//			.append(",")
//			.append(commonQueryBuilder.getQueryIntiator())
//			.append(CERTQUERYCONDITION).toString();
//
//			StringBuilder orderBy = new StringBuilder();
//			addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
//			addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
//			addFilter("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
//			addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues); 
//			addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
//			addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
//			addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
//			addFilter("eebd.gender", criteria.getGender(), query, preparedStmtValues);
//			addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
//			addDateRangeFilter("ebd.dateofbirth",  criteria.getDateOfBirthFrom(), criteria.getDateOfBirthTo(),query, preparedStmtValues);
//			addDateRangeFilter("ebd.fm_fileno",  criteria.getFromDateFile(), criteria.getToDateFile(), query, preparedStmtValues);
//
//
//			if (StringUtils.isEmpty(criteria.getSortBy()))
//			addOrderByColumns("ebd.createdtime",null, orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.dateOfBirth)
//			addOrderByColumns("ebd.dateofbirth",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.applicationNumber)
//			addOrderByColumns("ebd.applicationno",criteria.getSortOrder(),orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.mother)
//			addOrderByColumns("ebmi.firstname_en",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.gender)
//			addOrderByColumns("ebd.gender",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.registrationNo)
//			addOrderByColumns("ebd.registrationno",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.tenantId)
//			addOrderByColumns("ebd.tenantid",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.hospitalId)
//			addOrderByColumns("ebp.hospitalid",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.institutionId)
//			addOrderByColumns("ebp.institution_id",criteria.getSortOrder(), orderBy);
//			else if (criteria.getSortBy() == SearchCriteria.SortBy.wardCode)
//			addOrderByColumns("ebp.ward_id",criteria.getSortOrder(), orderBy);
//
//
//
//			addOrderToQuery(orderBy, query);
//			addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
//			return query.toString();
//			
//  }
   

    
}
