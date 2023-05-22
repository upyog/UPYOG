package org.ksmart.birth.birthnacregistry.repository.querybuilder;

import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
 
 
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
	            .append("ebd.tenantid as ba_tenantid,ebd.applicationtype as ba_applicationtype,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,ebd.registration_date as ba_registration_date,ebd.status as ba_status,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime,ebd.lastmodifiedby") .toString();
	           
	             
	            
	            

	 private static final String NACQUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id ")
	            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
	            .append(" LEFT JOIN eg_birth_applicant ebap ON ebap.birthdtlid = ebd.id").toString();
 
	 
	 private static final String NAC_QUERY_PLACE_OF_EVENT = new StringBuilder().append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.institution_id as pla_institution_id,ebp.placeofbirthid as pla_placeofbirthid,")
			 		.append("ebp.hospitalid as pla_hospitalid,ebp.ho_districtid as pla_ho_districtid,ebp.ho_villageid as pla_ho_villageid,ebp.ho_stateid as pla_ho_stateid").toString();
	
	 private static final String NAC_QUERY_MOTER_INFO = new StringBuilder().append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en") .toString();
	
	 private static final String QUERY_NACAPPLICANT = new StringBuilder().append("ebap.id as ebap_id,ebap.birthdtlid as ebap_birthdtlid,ebap.name_en as ebap_name_en,ebap.address_en as ebap_address_en,")
				.append("ebap.aadharno as ebap_aadharno,ebap.mobileno as ebap_mobileno,ebap.is_declared as ebap_is_declared,")
				.append("ebap.declaration_id as ebap_declaration_id,ebap.is_esigned as ebap_is_esigned").toString();

	 
	 private static final String QUERY = new StringBuilder().append("SELECT krbn.id as bn_id,krbn.registrationno as bn_registrationno,krbn.registration_date as bn_registration_date,krbn.registration_status as bn_registration_status,krbn.birthdetailsid as bn_birthdetailsid,krbn.applicant_name_en as bn_applicant_name_en,")
	            .append("krbn.care_of_applicant_name_en as bn_care_of_applicant_name_en,krbn.child_name_en as bn_child_name_en,krbn.dateofbirth as bn_dateofbirth,krbn.mother_name_en as bn_mother_name_en,krbn.birth_place_en as bn_birth_place_en,krbn.birth_districtid as bn_birth_districtid,krbn.birth_stateid as bn_birth_stateid,krbn.birth_villageid as bn_birth_villageid,krbn.createdby,krbn.createdtime,")
	            .append("krbn.lastmodifiedtime ,krbn.lastmodifiedby  ,krbn.applicationtype as bn_applicationtype,krbn.filestoreid as bn_filestoreid,krbn.status as bn_status,krbn.additionaldetail as bn_additionaldetail,")
	            .append("krbn.embeddedurl as bn_embeddedurl,krbn.dateofissue as bn_dateofissue,krbn.tenantid as bn_tenantid,krbn.certificateno as bn_certificateno,krbn.dateofreport as bn_dateofreport,krbn.ack_no as bn_ack_no,krbn.father_name_en as bn_father_name_en,krbn.perm_address as bn_perm_address,krbn.is_nac as isnac,krbn.is_nia as isnia")
	            .append(" FROM public.eg_birth_nac_registry krbn ").toString();
	             
	            

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
			    
			    
			    public String getRegBirthNacApplicationSearchQuery(@NotNull RegisterNacSearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {
			        StringBuilder query = new StringBuilder(QUERY);
			        StringBuilder orderBy = new StringBuilder();
			        addFilter("krbn.id", criteria.getId(), query, preparedStmtValues);
			        addFilter("krbn.ack_no", criteria.getApplicationNumber(), query, preparedStmtValues);
			        addFilter("krbn.tenantid", criteria.getTenantId(), query, preparedStmtValues);
			        addLikeFilter("LOWER(krbn.mother_name_en)", criteria.getNameOfMother(), query, preparedStmtValues);
			        
			        addLongFilter("krbn.dateofbirth", criteria.getBirthDate(), query, preparedStmtValues);
			        addLongFilter("krbn.registration_date", criteria.getRegistrationDate(), query, preparedStmtValues);
			        addFilter("krbn.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
			        addDateRangeFilter("krbn.dateofreport", criteria.getFromDate(), criteria.getToDate(), query, preparedStmtValues); 
			        addLikeFilter("LOWER(krbn.child_name_en)", criteria.getChildName(), query, preparedStmtValues);
 
			        if (StringUtils.isEmpty(criteria.getSortBy()))
			            addOrderByColumnsReg("krbn.createdtime",null, orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.birthDate)
			        	addOrderByColumnsReg("krbn.dateofbirth",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.registrationDate)
			        	addOrderByColumnsReg("krbn.registration_date",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.applicationNumber)
			        	addOrderByColumnsReg("krbn.ack_no",criteria.getSortOrder(),orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.nameOfMother)
			        	addOrderByColumnsReg("krbn.mother_name_en",criteria.getSortOrder(), orderBy); 
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.registrationNo)
			        	addOrderByColumnsReg("krbn.registrationno",criteria.getSortOrder(), orderBy); 
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.tenantId)
			        	addOrderByColumnsReg("krbn.tenantid",criteria.getSortOrder(), orderBy);
			        else if (criteria.getSortBy() == RegisterNacSearchCriteria.SortBy.childName)
			        	addOrderByColumnsReg("krbn.child_name_en",criteria.getSortOrder(), orderBy);
 

			        addOrderToQuery(orderBy, query);
			        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);			         
			        return query.toString();
			    }
			    void addOrderByColumnsReg(String column, RegisterNacSearchCriteria.SortOrder valueSort, StringBuilder orderBy){
			        addOrderClause(orderBy);
			        if(!StringUtils.isEmpty(column)){
			            addOrderClause(orderBy);
			            orderBy.append(column);
			            addAscDesc(valueSort, orderBy);
			        }
			    }
			    void addAscDesc(RegisterNacSearchCriteria.SortOrder valueSort, StringBuilder query){
			        if(valueSort == null)
			            query.append(" ASC, ");
			        else if(valueSort == RegisterNacSearchCriteria.SortOrder.ASC)
			            query.append(" ASC, ");
			        else
			            query.append(" DESC, ");
			    }
			    
}
