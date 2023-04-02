package org.ksmart.birth.birthnacregistry.repository.querybuilder;

import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class NacRegisterQueryBuilder extends BaseNacRegBuilder{

	 private static final String QUERYSTR = "";
	 private static final String NACQUERY = new StringBuilder().append("SELECt ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.firstname_en as ba_firstname_en,")
	            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,ebd.createdtime,")
	            .append("ebd.tenantid as ba_tenantid,ebd.applicationtype as ba_applicationtype,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,ebd.registration_date as ba_registration_date,ebd.status as ba_status") .toString();
	           
	             
	            
	            

	 private static final String NACQUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id ")
	            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'").toString();
//	            .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
//	            .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
//	            .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();

	 
	 private static final String NAC_QUERY_PLACE_OF_EVENT = new StringBuilder().append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid").toString();
	
	 private static final String NAC_QUERY_MOTER_INFO = new StringBuilder().append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,") .toString();
	
	 private static final String QUERY_NACAPPLICANT = new StringBuilder().append("ebap.id as ebap_id,ebap.birthdtlid as ebap_birthdtlid,ebap.name_en as ebap_name_en,ebap.address_en as ebap_address_en,")
				.append("ebap.aadharno as ebap_aadharno,ebap.mobileno as ebap_mobileno,ebap.is_declared as ebap_is_declared,")
				.append("ebap.declaration_id as ebap_declaration_id,ebap.is_esigned as ebap_is_esigned").toString();


	 public String getQueryMain() {
			return NACQUERY;
		}
		public String getQueryCondition() {
			return NACQUERYCONDITION;
		}
		public String getQueryPlaceOfEvent() {
			return NAC_QUERY_PLACE_OF_EVENT;
		}
		 

		public String getQueryMoterInfo() {
			return NAC_QUERY_MOTER_INFO;
		}
				
		public String getQueryNacApplicant() {
			return QUERY_NACAPPLICANT;
		}		
		
		  public String getApplicationSearchQueryForRegistry(@NotNull RegisterNacSearchCriteria criteria, @NotNull List<Object> preparedStmtValues) {
		        StringBuilder query = prepareSearchQuery();
		        prepareSearchCriteria(criteria, query, preparedStmtValues);
		        prepareOrderBy(criteria, query, preparedStmtValues);
		        return query.toString();
		    }
		    
		    
			 public StringBuilder prepareSearchQuery() {
				 StringBuilder query = new StringBuilder(QUERYSTR);
		    	 query.append(getQueryMain())
		                .append(",")               
		                .append(getQueryMoterInfo())
		                .append(",")
		                .append(getQueryPlaceOfEvent())
		                .append(",")                 
		                .append(getQueryNacApplicant())               
		                .append(NACQUERYCONDITION).toString();
			        return query;
			    }
			 
			 public StringBuilder prepareSearchCriteria(@NotNull RegisterNacSearchCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
				    addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
			        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
			        addFilter("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
			        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);        
			        addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
			        addDateRangeFilter("ebd.dateofbirth",  criteria.getFromDateReg(), criteria.getToDateReg(),query, preparedStmtValues);
			        return query;
			    }

			    public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query,AdoptionDetailRequest request, @NotNull List<Object> preparedStmtValues) {
			        if(preparedStmtValues.size() == 0) {
			            addFilter("ebd.createdby", request.getRequestInfo().getUserInfo().getUuid(), query, preparedStmtValues);
			            addFilter("ebd.status", "INITIATED", query, preparedStmtValues);
			        }
			        return query;
			    }

			    public StringBuilder prepareOrderBy(@NotNull RegisterNacSearchCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
			        StringBuilder orderBy = new StringBuilder();
			        if (StringUtils.isEmpty(criteria.getSortBy()))
			            addOrderByColumns("ebd.createdtime",null, orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.birthDate)
			            addOrderByColumns("ebd.dateofbirth",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.applicationNumber)
			            addOrderByColumns("ebd.applicationno",criteria.getSortOrder(),orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.nameOfMother)
			            addOrderByColumns("ebmi.firstname_en",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.gender)
			            addOrderByColumns("ebd.gender",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.registrationNo)
			            addOrderByColumns("ebd.registrationno",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.tenantId)
			            addOrderByColumns("ebd.tenantid",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.hospitalId)
			            addOrderByColumns("ebp.hospitalid",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.institutionId)
			            addOrderByColumns("ebp.institution_id",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.wardCode)
			            addOrderByColumns("ebp.ward_id",criteria.getSortOrder(), orderBy);
			       
			        addOrderToQuery(orderBy, query);
			        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
			        return query;
			    }
			    
}
