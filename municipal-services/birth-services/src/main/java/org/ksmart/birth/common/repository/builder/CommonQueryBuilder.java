package org.ksmart.birth.common.repository.builder;

import java.util.ArrayList;
import java.util.List;

import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.web.model.SearchCriteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

@Slf4j
@Component
public class CommonQueryBuilder extends BaseQueryBuilder {
	private static final String QUERY = new StringBuilder().append("SELECT ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.timeofbirth as ba_timeofbirth,")
			.append("ebd.am_pm as ba_am_pm,ebd.firstname_en as ba_firstname_en,ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,")
			.append("ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,ebd.tenantid as ba_tenantid,ebd.gender as ba_gender,ebd.remarks_en as ba_remarks_en,ebd.remarks_ml as ba_remarks_ml,")
			.append("ebd.aadharno as ba_aadharno,ebd.esign_user_code as ba_esign_user_code,ebd.esign_user_desig_code as ba_esign_user_desig_code,ebd.is_adopted as ba_is_adopted,ebd.is_abandoned as ba_is_abandoned,")
			.append("ebd.is_multiple_birth as ba_is_multiple_birth,ebd.is_father_info_missing as ba_is_father_info_missing,ebd.is_mother_info_missing as ba_is_mother_info_missing,")
			.append("ebd.no_of_alive_birth as ba_no_of_alive_birth,ebd.multiplebirthdetid as ba_multiplebirthdetid,ebd.is_born_outside as ba_is_born_outside,ebd.ot_passportno as ba_ot_passportno,")
			.append("ebd.ot_dateofarrival as ba_ot_dateofarrival,ebd.applicationtype as ba_applicationtype,ebd.businessservice as ba_businessservice,ebd.workflowcode as ba_workflowcode,")
			.append("ebd.fm_fileno as ba_fm_fileno,ebd.file_date as ba_file_date,ebd.file_status as ba_file_status,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,")
			.append("ebd.registration_date as ba_registration_date,ebd.action as ba_action,ebd.status as ba_status,")
			.append("ebd.adopt_deed_order_no as ba_adopt_deed_order_no,ebd.adopt_dateoforder_deed as ba_adopt_dateoforder_deed,ebd.adopt_issuing_auththority as ba_adopt_issuing_auththority,")
			.append("ebd.adopt_has_agency as ba_adopt_has_agency,ebd.adopt_agency_name as ba_adopt_agency_name,ebd.adopt_agency_address as ba_adopt_agency_address,ebd.is_stillbirth as ba_is_stillbirth,ebd.is_adopted as ba_is_adopted,")
			.append("ebd.adopt_decree_order_no as ba_adopt_decree_order_no,ebd.adopt_dateoforder_decree as ba_adopt_dateoforder_decree,ebd.adopt_agency_contact_person as ba_adopt_agency_contact_person,")
			.append("ebd.adopt_agency_contact_person_mobileno as ba_adopt_agency_contact_person_mobileno,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime,ebd.lastmodifiedby,ebd.nac_order_of_child as ba_nac_order_of_child,")
			.append("ebd.application_sub_type as  ba_application_sub_type, ebd.has_payment as  ba_has_payment, ebd.is_payment_success as  ba_is_payment_success, ebd.amount as  ba_amount, ebd.birthdate as  ba_birthdate,")
			.append("ebd.assignee as  ba_assignee, ebd.rdo_proceedings_no as  ba_rdo_proceedings_no, ebd.nac_registration_no as  ba_nac_registration_no, birthtime as ba_birthtime, birthdate_string as ba_birthdate_string ").toString();


	private static final String QUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='BIOLOGICAL'")
																	.append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
																	.append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
																	.append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
																	.append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
																	.append(" LEFT JOIN eg_birth_abandoned_care_taker ct ON ct.birthdtlid = ebd.id")
																	.append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id ").toString();

	
	private static final String QUERYCONDITIONADPTN = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
			.append(" LEFT JOIN eg_birth_application_document ebad ON ebad.birthdtlid = ebd.id")
			.append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id ").toString();
	
	private static final String QUERYCONDITIONADPTNREGISTRY = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='ADOPT'")
			.append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")			 
			.append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id ").toString();
	
	
   private static final String QUERYCONDITIONNAC = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='BIOLOGICAL'")
	        .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
	        .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
	        .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
	        .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
	        .append(" LEFT JOIN eg_birth_applicant ebap ON ebap.birthdtlid = ebd.id")
	        .append(" LEFT JOIN eg_birth_children_born ebcb ON ebcb.birthdtlid = ebd.id")
	        .append(" LEFT JOIN eg_birth_application_document ebad ON ebad.birthdtlid = ebd.id")
	        .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();
   
   private static final String QUERYCONDITIONNACREGISTRY = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id ")
	        .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id ")
	        .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id ")
	        .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id ")
	        .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
	        .append(" LEFT JOIN eg_birth_applicant ebap ON ebap.birthdtlid = ebd.id")	         
	        .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();
   
	private static final String QUERYCONDITIONCOMMON = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id  ")
			.append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id  ")
			.append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id  ")
			.append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id  ")
			.append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
			.append(" LEFT JOIN eg_birth_abandoned_care_taker ct ON ct.birthdtlid = ebd.id")
			.append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id ").toString();

	
	
	private static final String QUERY_PLACE_OF_EVENT = new StringBuilder().append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.placeofbirthid as pla_placeofbirthid,ebp.hospitalid as pla_hospitalid,ebp.public_place_id as pla_public_place_id,ebp.institution_type_id as pla_institution_type_id,")
			.append("ebp.institution_id as pla_institution_id,ebp.vehicletypeid as pla_vehicletypeid,ebp.vehicle_registration_no as pla_vehicle_registration_no,ebp.vehicle_from_en as pla_vehicle_from_en,")
			.append("ebp.vehicle_to_en as pla_vehicle_to_en,ebp.vehicle_from_ml as pla_vehicle_from_ml,ebp.vehicle_to_ml as pla_vehicle_to_ml,ebp.vehicle_admit_hospital_en as pla_vehicle_admit_hospital_en,")
			.append("ebp.ho_householder_en as pla_ho_householder_en,ebp.ho_locality_en as pla_ho_locality_en,ebp.ho_locality_ml as pla_ho_locality_ml,ebp.ho_street_name_en as pla_ho_street_name_en,")
			.append("ebp.ho_street_name_ml as pla_ho_street_name_ml,ebp.ho_housename_en as pla_ho_housename_en,ebp.ho_housename_ml as pla_ho_housename_ml,ebp.ho_villageid as pla_ho_villageid,")
			.append("ebp.ho_talukid as pla_ho_talukid,ebp.ho_districtid as pla_ho_districtid,ebp.ho_stateid as pla_ho_stateid,ebp.ho_poid as pla_ho_poid,ebp.ho_pinno as pla_ho_pinno,")
			.append("ebp.ho_countryid as pla_ho_countryid,ebp.ward_id as pla_ward_id,ebp.auth_officer_id as pla_auth_officer_id,ebp.auth_officer_desig_id as pla_auth_officer_desig_id,")
			.append("ebp.oth_auth_officer_name as pla_oth_auth_officer_name,ebp.oth_auth_officer_desig as pla_oth_auth_officer_desig,ebp.informantsaddress_en as pla_informantsaddress_en,")
			.append("ebp.informants_mobileno as pla_informants_mobileno,ebp.informants_aadhaar_no as pla_informants_aadhaar_no,ebp.is_inform_declare as pla_is_inform_declare,")
			.append("ebp.vehicle_haltplace_en as pla_vehicle_haltplace_en,ebp.vehicle_hospitalid as pla_vehicle_hospitalid,ebp.vehicle_haltplace_ml as pla_vehicle_haltplace_ml,")
			.append("ebp.vehicle_desc as pla_vehicle_desc,ebp.public_place_desc as pla_public_place_desc,ebp.public_locality_en as pla_public_locality_en,")
			.append("ebp.public_locality_ml as pla_public_locality_ml,ebp.public_street_name_en as pla_public_street_name_en,ebp.public_street_name_ml as pla_public_street_name_ml,")
			.append("ebp.ot_birth_place_en as pla_ot_birth_place_en, ebp.ot_birth_place_ml as pla_ot_birth_place_ml, ebp.ot_address1_en as pla_ot_address1_en, ebp.ot_address1_ml as pla_ot_address1_ml," )
			.append("ebp.ot_address2_en as pla_ot_address2_en, ebp.ot_address2_ml as pla_ot_address2_ml,ebp.ot_state_region_province_en as pla_ot_state_region_province_en, ebp.ot_state_region_province_ml as pla_ot_state_region_province_ml,")
			.append("ebp.ot_zipcode as pla_ot_zipcode, ebp.is_inform_declare as pla_is_inform_declare, ebp.relation as pla_relation,ebp.informantsname_en as pla_informantsname_en,ebp.is_born_outside as pla_is_born_outside," )
			.append("ebp.informants_office_name as pla_informants_office_name, ebp.ot_country as pla_ot_country, ebp.ot_town_village_en as pla_ot_town_village_en, ebp.ot_town_village_ml as pla_ot_town_village_ml,")
			.append("ebp.hosp_ip_op as pla_hosp_ip_op, ebp.hosp_ip_op_number as pla_hosp_ip_op_number, ebp.obstetrics_gync_number as pla_obstetrics_gync_number ")
			.toString();
	private static final String QUERY_FATER_INFO = new StringBuilder().append("ebfi.id as fa_id,ebfi.firstname_en as fa_firstname_en,ebfi.firstname_ml as fa_firstname_ml,")
			.append("ebfi.ot_passportno as fa_ot_passportno, ebfi.aadharno as fa_aadharno,ebfi.birthdtlid as fa_birthdtlid,ebfi.bio_adopt as fa_bio_adopt").toString();

	private static final String QUERY_MOTER_INFO = new StringBuilder().append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,ebmi.firstname_ml as mo_firstname_ml,")
			.append("ebmi.ot_passportno as mo_ot_passportno,ebmi.aadharno as mo_aadharno,ebmi.birthdtlid as mo_birthdtlid,ebmi.bio_adopt as mo_bio_adopt, ebmi.addressofmother as mo_addressofmother").toString();

	private static final String QUERY_PERMANANT_ADDRESS = new StringBuilder().append("eperad.id as per_id,eperad.housename_no_en as per_housename_no_en,")
			.append("eperad.housename_no_ml as per_housename_no_ml,eperad.ot_address1_en as per_ot_address1_en,eperad.ot_address1_ml as per_ot_address1_ml,eperad.ot_address2_en as per_ot_address2_en,")
			.append("eperad.ot_address2_ml as per_ot_address2_ml,eperad.ot_state_region_province_en as per_ot_state_region_province_en,eperad.ot_state_region_province_ml as per_ot_state_region_province_ml,")
			.append("eperad.ot_zipcode as per_ot_zipcode,eperad.villageid as per_villageid,eperad.village_name as per_village_name,eperad.tenantid as per_tenantid,eperad.talukid as per_talukid,")
			.append("eperad.taluk_name as per_taluk_name,eperad.ward_code as per_ward_code,eperad.locality_en as per_locality_en,")
			.append("eperad.locality_ml as per_locality_ml,eperad.street_name_en as per_street_name_en,eperad.street_name_ml as per_street_name_ml,eperad.districtid as per_districtid,")
			.append("eperad.stateid as per_stateid,eperad.poid as per_poid,eperad.pinno as per_pinno,eperad.countryid as per_countryid,eperad.birthdtlid as per_birthdtlid,")
			.append("eperad.bio_adopt as per_bio_adopt,eperad.same_as_present as per_same_as_present,eperad.family_emailid as per_family_emailid,eperad.family_mobileno as per_family_mobileno,")
			.append("eperad.postoffice_en as per_postoffice_en,eperad.postoffice_ml as per_postoffice_ml").toString();


	private static final String QUERY_PRESENT_ADDRESS = new StringBuilder().append("epreadd.id as pres_id,epreadd.housename_no_en as pres_housename_no_en,")
			.append("epreadd.housename_no_ml as pres_housename_no_ml,epreadd.ot_address1_en as pres_ot_address1_en,epreadd.ot_address1_ml as pres_ot_address1_ml,epreadd.ot_address2_en as pres_ot_address2_en,")
			.append("epreadd.ot_address2_ml as pres_ot_address2_ml,epreadd.ot_state_region_province_en as pres_ot_state_region_province_en,epreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,")
			.append("epreadd.ot_zipcode as pres_ot_zipcode,epreadd.villageid as pres_villageid,epreadd.village_name as pres_village_name,epreadd.tenantid as pres_tenantid,epreadd.talukid as pres_talukid,")
			.append("epreadd.taluk_name as pres_taluk_name,epreadd.ward_code as pres_ward_code,epreadd.locality_en as pres_locality_en,")
			.append("epreadd.locality_ml as pres_locality_ml,epreadd.street_name_en as pres_street_name_en,epreadd.street_name_ml as pres_street_name_ml,epreadd.districtid as pres_districtid,")
			.append("epreadd.stateid as pres_stateid,epreadd.poid as pres_poid,epreadd.pinno as pres_pinno,epreadd.countryid as pres_countryid,epreadd.birthdtlid as pres_birthdtlid,")
			.append("epreadd.bio_adopt as pres_bio_adopt,epreadd.postoffice_en as pres_postoffice_en,epreadd.postoffice_ml as pres_postoffice_ml").toString();


	private static final String QUERY_STATISTICAL_INFO = new StringBuilder().append("estat.id as stat_id,estat.weight_of_child as stat_weight_of_child,")
			.append("estat.duration_of_pregnancy_in_week as stat_duration_of_pregnancy_in_week,estat.nature_of_medical_attention as stat_nature_of_medical_attention,estat.cause_of_foetal_death as stat_cause_of_foetal_death,")
            .append("estat.delivery_method as stat_delivery_method,estat.religionid as stat_religionid,estat.father_nationalityid as stat_father_nationalityid,estat.father_educationid as stat_father_educationid,")
            .append("estat.father_proffessionid as stat_father_proffessionid,estat.mother_educationid as stat_mother_educationid,estat.mother_proffessionid as stat_mother_proffessionid," )
            .append("estat.mother_nationalityid as stat_mother_nationalityid,estat.mother_age_marriage as stat_mother_age_marriage,estat.mother_age_delivery as stat_mother_age_delivery," )
            .append("estat.mother_no_of_birth_given as stat_mother_no_of_birth_given,estat.mother_maritalstatusid as stat_mother_maritalstatusid,estat.mother_unmarried as stat_mother_unmarried,")
            .append("estat.mother_res_lbid as stat_mother_res_lbid,estat.mother_res_lb_code_id as stat_mother_res_lb_code_id,estat.mother_res_place_type_id as stat_mother_res_place_type_id,")
            .append("estat.mother_res_lb_type_id as stat_mother_res_lb_type_id,estat.mother_res_district_id as stat_mother_res_district_id,estat.mother_res_state_id as stat_mother_res_state_id," )
            .append("estat.mother_res_country_id as stat_mother_res_country_id,estat.mother_resdnce_addr_type as stat_mother_resdnce_addr_type,estat.mother_resdnce_tenant as stat_mother_resdnce_tenant,")
            .append("estat.mother_resdnce_placetype as stat_mother_resdnce_placetype,estat.mother_resdnce_place_en as stat_mother_resdnce_place_en,estat.mother_resdnce_place_ml as stat_mother_resdnce_place_ml,")
            .append("estat.mother_resdnce_lbtype as stat_mother_resdnce_lbtype,estat.mother_resdnce_district as stat_mother_resdnce_district,estat.mother_resdnce_state as stat_mother_resdnce_state," )
            .append("estat.mother_resdnce_country as stat_mother_resdnce_country,estat.birthdtlid as stat_birthdtlid,estat.mother_order_of_cur_delivery as stat_mother_order_of_cur_delivery,")
            .append("estat.mother_order_cur_child as stat_mother_order_cur_child,estat.mother_res_no_of_years as stat_mother_res_no_of_years").toString();

	private static final String QUERY_INTIATOR = new StringBuilder().append("ini.id as ini_id,ini.birthdtlid as ini_birthdtlid,ini.initiator_name as ini_initiator_name,ini.initiator_institution as ini_initiator_institution,")
			.append("ini.initiator_inst_desig as ini_initiator_inst_desig,ini.relation as ini_relation,ini.initiator_address as ini_initiator_address,")
			.append("ini.is_declared as ini_is_declared,ini.declaration_id as ini_declaration_id,ini.aadharno as ini_aadharno,ini.mobileno as ini_mobileno,ini.is_care_taker as ini_is_care_taker,ini.is_esigned as ini_is_esigned,")
			.append("ini.initiator as ini_initiator, ini.isguardian as ini_isguardian")
			.toString();
	
	private static final String QUERY_OTHERCH = new StringBuilder().append("ebcb.id as ebcb_id,ebcb.birthdtlid as ebcb_birthdtlid,ebcb.child_name_en as ebcb_child_name_en,ebcb.child_name_ml as ebcb_child_name_ml,")
			.append("ebcb.sex as ebcb_sex,ebcb.order_of_birth as ebcb_order_of_birth,ebcb.dob as ebcb_dob,")
			.append("ebcb.is_alive as ebcb_is_alive").toString();
	
	private static final String QUERY_NACAPPLICANT = new StringBuilder().append("ebap.id as ebap_id,ebap.birthdtlid as ebap_birthdtlid,ebap.name_en as ebap_name_en,ebap.address_en as ebap_address_en,")
			.append("ebap.aadharno as ebap_aadharno,ebap.mobileno as ebap_mobileno,ebap.is_declared as ebap_is_declared,")
			.append("ebap.declaration_id as ebap_declaration_id,ebap.is_esigned as ebap_is_esigned,ebap.care_of_applicant as ebap_care_of_applicant").toString();
	
	private static final String QUERY_NACDOCUMENTS = new StringBuilder().append("ebad.id as ebad_id,ebad.birthdtlid as ebad_birthdtlid,ebad.document_name as ebad_document_name,ebad.document_type as ebad_document_type,")
			.append("ebad.document_description as ebad_document_description,ebad.filestoreid as ebad_filestoreid,ebad.document_link as ebad_document_link,")
			.append("ebad.file_type as ebad_file_type,ebad.file_size as ebad_file_size,ebad.active as ebad_active").toString();

	private static final String QUERY_CARETAKE_ABAN = new StringBuilder().append("ct.id as ct_id, ct.birthdtlid as ct_birthdtlid, ct.care_taker_name as ct_care_taker_name," )
																		.append("ct.care_taker_institution as ct_care_taker_institution, ct.care_taker_inst_designation as ct_care_taker_inst_designation,")
																		.append("ct.care_taker_address as ct_care_taker_address, ct.care_taker_mobileno as ct_care_taker_mobileno").toString();


	private static final String QUERY_COUNT = new StringBuilder().append("SELECT COUNT(*) ").toString();

	public String getQueryMain() {
		return QUERY;
	}
	public String getQueryCondition() {
		return QUERYCONDITION;
	}
	
	public String getQueryConditionCommon() {
		return QUERYCONDITIONCOMMON;
	}
	public String getQueryConditionAdptn() {
		return QUERYCONDITIONADPTN;
	}
	
	public String getQueryConditionAdptnReg() {
		return QUERYCONDITIONADPTNREGISTRY;
	}
	
	public String getQueryConditionNac() {
		return QUERYCONDITIONNAC;
	}	
	
	public String getQueryConditionNacReg() {
		return QUERYCONDITIONNACREGISTRY;
	}
	public String getQueryPlaceOfEvent() {
		return QUERY_PLACE_OF_EVENT;
	}
	public String getQueryFaterInfo() {
		return QUERY_FATER_INFO;
	}

	public String getQueryMoterInfo() {
		return QUERY_MOTER_INFO;
	}

	public String getQueryPermanant() {
		return QUERY_PERMANANT_ADDRESS;
	}

	public String getQueryPresent() {
		return QUERY_PRESENT_ADDRESS;
	}

	public String getQueryStat() {
		return QUERY_STATISTICAL_INFO;
	}

	public String getQueryIntiator() {
		return QUERY_INTIATOR;
	}
	public String getQueryNacApplicant() {
		return QUERY_NACAPPLICANT;
	}	
	public String getQueryOtherChildren() {
		return QUERY_OTHERCH;
	}
	public String getQueryDocuments() {
		return QUERY_NACDOCUMENTS;
	}
	public String getQueryCareTaker() {
		return QUERY_CARETAKE_ABAN;
	}
	public String getCountQuery() {
		return QUERY_COUNT;
	}



	public StringBuilder prepareSearchCriteria(@NotNull SearchCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
		addFilter("ebd.createdby", criteria.getUuid(), query, preparedStmtValues);
		addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
		addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
		addFilters("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
		addFilter("ebd.applicationno", criteria.getAppNumber(), query, preparedStmtValues);
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
		addLikeFilter("LOWER(kbmi.firstname_en)", criteria.getNameOfMother(), query, preparedStmtValues);
		addLikeFilter("LOWER(krbd.firstname_en)", criteria.getChildName(), query, preparedStmtValues);
		addLikeFilter("LOWER(kbfi.firstname_en)", criteria.getNameOfFather(), query, preparedStmtValues);

		return query;
	}

//	public StringBuilder prepareSearchCriteriaFromRequest(StringBuilder query, String uuid, @NotNull List<Object> preparedStmtValues) {
//		if(preparedStmtValues.size() == 0 && uuid != null) {
//			addFilter("ebd.createdby", uuid, query, preparedStmtValues);
//			List<String> statusList = new ArrayList<>();
//			statusList.add(BirthConstants.STATUS_INITIATED);
//			statusList.add(BirthConstants.STATUS_FOR_PAYMENT);
//			statusList.add(BirthConstants.STATUS_CITIZENACTIONREQUIRED);
//			addFilters("ebd.status", statusList, query, preparedStmtValues);
//		}
//		return query;
//	}

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

	public String getBirthApplicationSearchQuery(@NotNull SearchCriteria criteria, 	@NotNull List<Object> preparedStmtValues, Boolean isCount) {
		StringBuilder query = prepareSearchQueryCommon();
		prepareSearchCriteria(criteria, query, preparedStmtValues);
		prepareOrderBy(criteria, query, preparedStmtValues);
		return query.toString();
	}
	public int searchBirthCount(SearchCriteria criteria, JdbcTemplate jdbcTemplate) {
		List<Object> preparedStmtValues = new ArrayList<>();
		String queryCnt = getCountSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
		return jdbcTemplate.queryForObject(queryCnt,preparedStmtValues.toArray(),Integer.class);
	}
	public String getCountSearchQuery(@NotNull SearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {
		StringBuilder query = prepareSearchCountQueryCommon();
		prepareSearchCriteria(criteria, query, preparedStmtValues);
		//prepareOrderBy(criteria, query, preparedStmtValues);
		return query.toString();
	}

	public String getBirthApplicationSearchQueryCommon(@NotNull SearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {
		StringBuilder query = prepareSearchQueryCommon();
		prepareSearchCriteria(criteria, query, preparedStmtValues);
		prepareOrderBy(criteria, query, preparedStmtValues);
		return query.toString();
	}

	public String getApplicationSearchQueryForRegistry(@NotNull SearchCriteria criteria, @NotNull List<Object> preparedStmtValues) {
		StringBuilder query = prepareSearchQueryCommon();
		prepareSearchCriteria(criteria, query, preparedStmtValues);
		prepareOrderBy(criteria, query, preparedStmtValues);
		return query.toString();
	}

	public StringBuilder prepareSearchQuery() {
		StringBuilder query = new StringBuilder();
		query.append(getQueryMain())
				.append(",")
				.append(getQueryPlaceOfEvent())
				.append(",")
				.append(getQueryFaterInfo())
				.append(",")
				.append(getQueryMoterInfo())
				.append(",")
				.append(getQueryPresent())
				.append(",")
				.append(getQueryPermanant())
				.append(",")
				.append(getQueryStat())
				.append(",")
				.append(getQueryIntiator())
				.append(",")
				.append(getQueryCareTaker())
				.append(getQueryCondition()).toString();
		return query;
	}
	
	public StringBuilder prepareSearchQueryCommon() {
		StringBuilder query = new StringBuilder();
		query.append(getQueryMain())
				.append(",")
				.append(getQueryPlaceOfEvent())
				.append(",")
				.append(getQueryFaterInfo())
				.append(",")
				.append(getQueryMoterInfo())
				.append(",")
				.append(getQueryPresent())
				.append(",")
				.append(getQueryPermanant())
				.append(",")
				.append(getQueryStat())
				.append(",")
				.append(getQueryIntiator())
				.append(",")
				.append(getQueryCareTaker())
				.append(getQueryConditionCommon()).toString();
		return query;
	}

	public StringBuilder prepareSearchCountQueryCommon() {
		StringBuilder query = new StringBuilder();
		query.append(getCountQuery()).append(getQueryConditionCommon()).toString();
		return query;
	}

}





