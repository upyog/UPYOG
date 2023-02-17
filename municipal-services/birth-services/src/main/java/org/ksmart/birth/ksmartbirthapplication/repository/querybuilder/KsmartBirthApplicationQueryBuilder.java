package org.ksmart.birth.ksmartbirthapplication.repository.querybuilder;


import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationSearchCriteria;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class KsmartBirthApplicationQueryBuilder extends KsmartBaseBirthQuery {
    private static final String QUERY = new StringBuilder().append("SELECt ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.timeofbirth as ba_timeofbirth,ebd.am_pm as ba_am_pm,ebd.firstname_en as ba_firstname_en,")
            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,")
            .append("ebd.tenantid as ba_tenantid,ebd.gender as ba_gender,ebd.remarks_en as ba_remarks_en,ebd.remarks_ml as ba_remarks_ml,ebd.aadharno as ba_aadharno,ebd.esign_user_code as ba_esign_user_code,")
            .append("ebd.esign_user_desig_code as ba_esign_user_desig_code,ebd.is_adopted as ba_is_adopted,ebd.is_abandoned as ba_is_abandoned,ebd.is_multiple_birth as ba_is_multiple_birth,")
            .append(" ebd.is_father_info_missing as ba_is_father_info_missing,ebd.is_mother_info_missing as ba_is_mother_info_missing,ebd.no_of_alive_birth as ba_no_of_alive_birth,ebd.multiplebirthdetid as ba_multiplebirthdetid,")
            .append(" ebd.is_born_outside as ba_is_born_outside,ebd.ot_passportno as ba_ot_passportno,ebd.ot_dateofarrival as ba_ot_dateofarrival,ebd.applicationtype as ba_applicationtype,ebd.businessservice as ba_businessservice,")
            .append("ebd.workflowcode as ba_workflowcode,ebd.fm_fileno as ba_fm_fileno,ebd.file_date as ba_file_date,ebd.file_status as ba_file_status,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,")
            .append("ebd.registration_date as ba_registration_date,ebd.action as ba_action,ebd.status as ba_status,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime ,")
            .append("ebd.lastmodifiedby ,ebd.adopt_firstname_en as ba_adopt_firstname_en,ebd.adopt_firstname_ml as ba_adopt_firstname_ml,ebd.adopt_middlename_en as ba_adopt_middlename_en,")
            .append("ebd.adopt_middlename_ml as ba_adopt_middlename_ml,ebd.adopt_lastname_en as ba_adopt_lastname_en,ebd.adopt_lastname_ml as ba_adopt_lastname_ml,ebd.adopt_deed_order_no as ba_adopt_deed_order_no,")
            .append("ebd.adopt_dateoforder_deed as ba_adopt_dateoforder_deed,ebd.adopt_issuing_auththority as ba_adopt_issuing_auththority,ebd.adopt_has_agency as ba_adopt_has_agency,ebd.adopt_agency_name as ba_adopt_agency_name,")
            .append("ebd.adopt_agency_address as ba_adopt_agency_address,ebfi.id as fa_id,ebfi.firstname_en as fa_firstname_en,ebfi.firstname_ml as fa_firstname_ml,ebfi.middlename_en as fa_middlename_en,")
            .append("ebfi.middlename_ml as fa_middlename_ml,ebfi.lastname_en as fa_lastname_en,ebfi.lastname_ml as fa_lastname_ml,ebfi.aadharno as fa_aadharno,ebfi.ot_passportno as fa_ot_passportno,ebfi.emailid as fa_emailid,")
            .append("ebfi.mobileno as fa_mobileno,ebfi.createdtime,ebfi.createdby,ebfi.lastmodifiedtime,ebfi.lastmodifiedby,")
            .append("ebfi.birthdtlid as fa_birthdtlid,ebfi.bio_adopt as fa_bio_adopt,ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,ebmi.firstname_ml as mo_firstname_ml,ebmi.middlename_en as mo_middlename_en,")
            .append("ebmi.middlename_ml as mo_middlename_ml,ebmi.lastname_en as mo_lastname_en,ebmi.lastname_ml as mo_lastname_ml,ebmi.aadharno as mo_aadharno,ebmi.ot_passportno as mo_ot_passportno,ebmi.emailid as mo_emailid,")
            .append("ebmi.mobileno as mo_mobileno,ebmi.createdtime,ebmi.createdby,ebmi.lastmodifiedtime ,ebmi.lastmodifiedby ,")
            .append("ebmi.birthdtlid as mo_birthdtlid,ebmi.bio_adopt as mo_bio_adopt,eperad.id as per_id,eperad.resdnce_addr_type as per_resdnce_addr_type,")
            .append("eperad.res_asso_no as per_res_asso_no,eperad.housename_no_en as per_housename_no_en,eperad.housename_no_ml as per_housename_no_ml,eperad.ot_address1_en as per_ot_address1_en,")
            .append("eperad.ot_address1_ml as per_ot_address1_ml,eperad.ot_address2_en as per_ot_address2_en,eperad.ot_address2_ml as per_ot_address2_ml,")
            .append("eperad.villageid as per_villageid,eperad.tenantid as per_tenantid,eperad.talukid as per_talukid,")
            .append("eperad.districtid as per_districtid,eperad.stateid as per_stateid,eperad.poid as per_poid,eperad.pinno as per_pinno,eperad.ot_state_region_province_en as per_ot_state_region_province_en,")
            .append("eperad.ot_state_region_province_ml as per_ot_state_region_province_ml,eperad.countryid as per_countryid,eperad.createdby ,eperad.createdtime,")
            .append("eperad.lastmodifiedby ,eperad.lastmodifiedtime,eperad.birthdtlid as per_birthdtlid,eperad.same_as_present as per_same_as_present,eperad.bio_adopt as per_bio_adopt,eperad.res_asso_no_ml as per_res_asso_no_ml,")
            .append("eperad.taluk_name as per_taluk_name, eperad.village_name as per_village_name, eperad.ward_code as per_ward_code, eperad.doorno as per_doorno, eperad.subno as per_subno , eperad.ot_zipcode as per_ot_zipcode, eperad.locality_en as per_locality_en,")
            .append("eperad.street_name_en as per_street_name_en, eperad.locality_ml as per_locality_ml, eperad.street_name_ml as per_street_name_ml,")
            .append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.placeofbirthid as pla_placeofbirthid,ebp.hospitalid as pla_hospitalid,ebp.vehicletypeid as pla_vehicletypeid,")
            .append("ebp.vehicle_registration_no as pla_vehicle_registration_no,ebp.vehicle_from_en as pla_vehicle_from_en,ebp.vehicle_to_en as pla_vehicle_to_en,ebp.vehicle_from_ml as pla_vehicle_from_ml,")
            .append("ebp.vehicle_to_ml as pla_vehicle_to_ml,ebp.vehicle_other_en as pla_vehicle_other_en,ebp.vehicle_other_ml as pla_vehicle_other_ml,ebp.vehicle_admit_hospital_en as pla_vehicle_admit_hospital_en,")
            .append("ebp.vehicle_admit_hospital_ml as pla_vehicle_admit_hospital_ml,ebp.public_place_id as pla_public_place_id,ebp.ho_householder_en as pla_ho_householder_en,ebp.ho_householder_ml as pla_ho_householder_ml,")
            .append("ebp.ho_buildingno as pla_ho_buildingno,ebp.ho_res_asso_no as pla_ho_res_asso_no,ebp.ho_houseno as pla_ho_houseno,ebp.ho_housename_en as pla_ho_housename_en,ebp.ho_housename_ml as pla_ho_housename_ml,")
            .append("ebp.ho_villageid as pla_ho_villageid,ebp.ho_talukid as pla_ho_talukid,ebp.ho_districtid as pla_ho_districtid,ebp.ho_locality_en as pla_ho_locality_en, ebp.ho_locality_ml as pla_ho_locality_ml,")
            .append("ebp.ho_street_name_en as pla_ho_street_name_en, ebp.ho_street_name_ml as pla_ho_street_name_ml,")
            .append("ebp.ho_stateid as pla_ho_stateid,ebp.ho_poid as pla_ho_poid,ebp.ho_pinno as pla_ho_pinno,ebp.ho_countryid as pla_ho_countryid,")
            .append("ebp.ward_id as pla_ward_id,ebp.oth_details_en as pla_oth_details_en,ebp.oth_details_ml as pla_oth_details_ml,ebp.institution_type_id as pla_institution_type_id,ebp.institution_id as pla_institution_id,")
            .append("ebp.auth_officer_id as pla_auth_officer_id,ebp.auth_officer_desig_id as pla_auth_officer_desig_id,ebp.oth_auth_officer_name as pla_oth_auth_officer_name,")
            .append("ebp.oth_auth_officer_desig as pla_oth_auth_officer_desig,ebp.informantsname_en as pla_informantsname_en,ebp.informantsname_ml as pla_informantsname_ml,")
            .append("ebp.informantsaddress_en as pla_informantsaddress_en,ebp.informantsaddress_ml as pla_informantsaddress_ml,ebp.informants_mobileno as pla_informants_mobileno,")
            .append("ebp.informants_aadhaar_no as pla_informants_aadhaar_no,ebp.is_born_outside as pla_is_born_outside,ebp.createdtime ,ebp.createdby,")
            .append("ebp.lastmodifiedtime,ebp.lastmodifiedby,epreadd.id as pres_id,epreadd.resdnce_addr_type as pres_resdnce_addr_type,epreadd.buildingno as pres_buildingno,")
            .append("epreadd.houseno as pres_houseno,epreadd.res_asso_no as pres_res_asso_no,epreadd.housename_no_en as pres_housename_no_en,epreadd.housename_no_ml as pres_housename_no_ml,epreadd.ot_address1_en as pres_ot_address1_en,")
            .append("epreadd.ot_address1_ml as pres_ot_address1_ml,epreadd.ot_address2_en as pres_ot_address2_en,epreadd.ot_address2_ml as pres_ot_address2_ml,")
            .append("epreadd.villageid as pres_villageid,epreadd.tenantid as pres_tenantid,epreadd.talukid as pres_talukid,")
            .append("epreadd.districtid as pres_districtid,epreadd.stateid as pres_stateid,epreadd.poid as pres_poid,epreadd.pinno as pres_pinno,epreadd.ot_state_region_province_en as pres_ot_state_region_province_en,")
            .append("epreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,epreadd.countryid as pres_countryid,epreadd.createdby,epreadd.createdtime,")
            .append("epreadd.lastmodifiedby,epreadd.lastmodifiedtime ,epreadd.birthdtlid as pres_birthdtlid,epreadd.bio_adopt as pres_bio_adopt,epreadd.res_asso_no_ml as pres_res_asso_no_ml,")
            .append("epreadd.taluk_name as pres_taluk_name, epreadd.village_name as pres_village_name, epreadd.ward_code as pres_ward_code, epreadd.doorno as pres_doorno, epreadd.subno as pres_subno , epreadd.ot_zipcode as pres_ot_zipcode, epreadd.locality_en as pres_locality_en,")
            .append("epreadd.street_name_en as pres_street_name_en, epreadd.locality_ml as pres_locality_ml, epreadd.street_name_ml as pres_street_name_ml,")
            .append("estat.id as stat_id,estat.weight_of_child as stat_weight_of_child,")
            .append("estat.duration_of_pregnancy_in_week as stat_duration_of_pregnancy_in_week,estat.nature_of_medical_attention as stat_nature_of_medical_attention,estat.way_of_pregnancy as stat_way_of_pregnancy,")
            .append("estat.delivery_method as stat_delivery_method,estat.deliverytypeothers_en as stat_deliverytypeothers_en,estat.deliverytypeothers_ml as stat_deliverytypeothers_ml,estat.religionid as stat_religionid,")
            .append("estat.father_nationalityid as stat_father_nationalityid,estat.father_educationid as stat_father_educationid,estat.father_education_subid as stat_father_education_subid,")
            .append("estat.father_proffessionid as stat_father_proffessionid,estat.mother_educationid as stat_mother_educationid,estat.mother_education_subid as stat_mother_education_subid,")
            .append("estat.mother_proffessionid as stat_mother_proffessionid,estat.mother_nationalityid as stat_mother_nationalityid,estat.mother_age_marriage as stat_mother_age_marriage,")
            .append("estat.mother_age_delivery as stat_mother_age_delivery,estat.mother_no_of_birth_given as stat_mother_no_of_birth_given,estat.mother_maritalstatusid as stat_mother_maritalstatusid,")
            .append("estat.mother_unmarried as stat_mother_unmarried,estat.mother_res_lbid as stat_mother_res_lbid,estat.mother_res_lb_code_id as stat_mother_res_lb_code_id,")
            .append("estat.mother_res_place_type_id as stat_mother_res_place_type_id,estat.mother_res_lb_type_id as stat_mother_res_lb_type_id,estat.mother_res_district_id as stat_mother_res_district_id,")
            .append("estat.mother_res_state_id as stat_mother_res_state_id,estat.mother_res_country_id as stat_mother_res_country_id,estat.mother_resdnce_addr_type as stat_mother_resdnce_addr_type,")
            .append("estat.mother_resdnce_tenant as stat_mother_resdnce_tenant,estat.mother_resdnce_placetype as stat_mother_resdnce_placetype,estat.mother_resdnce_place_en as stat_mother_resdnce_place_en,")
            .append("estat.mother_resdnce_place_ml as stat_mother_resdnce_place_ml,estat.mother_resdnce_lbtype as stat_mother_resdnce_lbtype,estat.mother_resdnce_district as stat_mother_resdnce_district,")
            .append("estat.mother_resdnce_state as stat_mother_resdnce_state,estat.mother_resdnce_country as stat_mother_resdnce_country,estat.birthdtlid as stat_birthdtlid,estat.createdby,")
            .append("estat.createdtime,estat.lastmodifiedtime,estat.lastmodifiedby,estat.mother_order_of_cur_delivery as stat_mother_order_of_cur_delivery,")
            .append("estat.mother_order_cur_child as stat_mother_order_cur_child,estat.mother_res_no_of_years as stat_mother_res_no_of_years")
            .append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id").toString();

    public String getKsmartBirthApplicationSearchQuery(@NotNull KsmartBirthApplicationSearchCriteria criteria,
                                                 @NotNull List<Object> preparedStmtValues, Boolean isCount) {
        StringBuilder query = new StringBuilder(QUERY);

        addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilter("ebd.applicationno", criteria.getApplicationNo(), query, preparedStmtValues);
        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);
        addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
        addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
        addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);


        addDateRangeFilter("ebd.dateofreport",
                criteria.getFromDate(),
                criteria.getToDate(),
                query,
                preparedStmtValues);

        addDateRangeFilter("ebd.file_date",
                criteria.getFromDateFile(),
                criteria.getToDateFile(),
                query,
                preparedStmtValues);
        return query.toString();
    }

}
