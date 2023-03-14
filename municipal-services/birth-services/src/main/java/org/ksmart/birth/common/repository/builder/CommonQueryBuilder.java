package org.ksmart.birth.common.repository.builder;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonQueryBuilder {

	private static final String QUERY_PLACE_OF_EVENT = new StringBuilder().append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.placeofbirthid as pla_placeofbirthid,ebp.hospitalid as pla_hospitalid,ebp.public_place_id as pla_public_place_id,ebp.institution_type_id as pla_institution_type_id,")
			.append("ebp.institution_id as pla_institution_id,ebp.vehicletypeid as pla_vehicletypeid,ebp.vehicle_registration_no as pla_vehicle_registration_no,ebp.vehicle_from_en as pla_vehicle_from_en,")
			.append("ebp.vehicle_to_en as pla_vehicle_to_en,ebp.vehicle_from_ml as pla_vehicle_from_ml,ebp.vehicle_to_ml as pla_vehicle_to_ml,ebp.vehicle_admit_hospital_en as pla_vehicle_admit_hospital_en,")
			.append("ebp.ho_householder_en as pla_ho_householder_en,ebp.ho_locality_en as pla_ho_locality_en,ebp.ho_locality_ml as pla_ho_locality_ml,ebp.ho_street_name_en as pla_ho_street_name_en,")
			.append("ebp.ho_street_name_ml as pla_ho_street_name_ml,ebp.ho_housename_en as pla_ho_housename_en,ebp.ho_housename_ml as pla_ho_housename_ml,ebp.ho_villageid as pla_ho_villageid,")
			.append("ebp.ho_talukid as pla_ho_talukid,ebp.ho_districtid as pla_ho_districtid,ebp.ho_stateid as pla_ho_stateid,ebp.ho_poid as pla_ho_poid,ebp.ho_pinno as pla_ho_pinno,")
			.append("ebp.ho_countryid as pla_ho_countryid,ebp.ward_id as pla_ward_id,ebp.auth_officer_id as pla_auth_officer_id,ebp.auth_officer_desig_id as pla_auth_officer_desig_id,")
			.append("ebp.oth_auth_officer_name as pla_oth_auth_officer_name,ebp.oth_auth_officer_desig as pla_oth_auth_officer_desig,ebp.informantsaddress_en as pla_informantsaddress_en,")
			.append("ebp.informants_mobileno as pla_informants_mobileno,ebp.informants_aadhaar_no as pla_informants_aadhaar_no,ebp.is_inform_declare as pla_is_inform_declare,")
			.append("ebp.vehicle_haltplace_en as pla_vehicle_haltplace_en,ebp.vehicle_hospitalid as pla_vehicle_hospitalid,")
			.append("ebp.vehicle_haltplace_ml as pla_vehicle_haltplace_ml,ebp.vehicle_desc as pla_vehicle_desc,")
			.append("ebp.public_place_desc as pla_public_place_desc,ebp.public_locality_en as pla_public_locality_en,ebp.public_locality_ml as pla_public_locality_ml,ebp.public_street_name_en as pla_public_street_name_en,ebp.public_street_name_ml as pla_public_street_name_ml").toString();
	private static final String QUERY_FATER_INFO = new StringBuilder().append("ebfi.id as fa_id,ebfi.firstname_en as fa_firstname_en,ebfi.firstname_ml as fa_firstname_ml,")
			.append("ebfi.aadharno as fa_aadharno,ebfi.birthdtlid as fa_birthdtlid,ebfi.bio_adopt as fa_bio_adopt").toString();

	private static final String QUERY_MOTER_INFO = new StringBuilder().append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,ebmi.firstname_ml as mo_firstname_ml,ebmi.aadharno as mo_aadharno,ebmi.birthdtlid as mo_birthdtlid,ebmi.bio_adopt as mo_bio_adopt").toString();

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
			.append("ini.is_declared as ini_is_declared,ini.declaration_id as ini_declaration_id,ini.aadharno as ini_aadharno,ini.mobileno as ini_mobileno,ini.is_care_taker as ini_is_care_taker,ini.is_esigned as ini_is_esigned").toString();

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

}





