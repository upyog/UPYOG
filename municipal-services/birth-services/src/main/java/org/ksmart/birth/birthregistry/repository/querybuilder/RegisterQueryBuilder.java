package org.ksmart.birth.birthregistry.repository.querybuilder;


import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class RegisterQueryBuilder extends BaseRegBuilder {
    private static final String QUERY=new StringBuilder().append("SELECT krbd.id,	krbd.dateofreport,	krbd.dateofbirth,	krbd.timeofbirth,	krbd.am_pm,	krbd.firstname_en,	krbd.firstname_ml,	krbd.middlename_en,")
            .append("krbd.middlename_ml,	krbd.lastname_en,	krbd.lastname_ml,	krbd.tenantid,	krbd.gender,	krbd.remarks_en,	krbd.remarks_ml,	krbd.aadharno,")
            .append("krbd.createdtime,	krbd.createdby,	krbd.lastmodifiedtime,	krbd.lastmodifiedby,	krbd.esign_user_code,	krbd.esign_user_desig_code,	krbd.is_adopted,")
            .append("krbd.is_abandoned,	krbd.is_multiple_birth,	krbd.is_father_info_missing,	krbd.is_mother_info_missing,	krbd.no_of_alive_birth,	krbd.multiplebirthdetid,")
            .append("krbd.ot_passportno,	krbd.registrationno,	krbd.registration_status,	krbd.registration_date,	krbd.is_born_outside,	krbd.ot_dateofarrival, kbfi.id,")
            .append("kbp.id, kbp.birthdtlid, kbp.placeofbirthid, kbp.hospitalid, kbp.vehicletypeid, kbp.vehicle_registration_no, kbp.vehicle_from_en, kbp.vehicle_to_en, ")
            .append("kbp.vehicle_from_ml, kbp.vehicle_to_ml, kbp.vehicle_other_en, kbp.vehicle_other_ml, kbp.vehicle_admit_hospital_en, kbp.vehicle_admit_hospital_ml, ")
            .append("kbp.public_place_id, kbp.ho_householder_en, kbp.ho_householder_ml, kbp.ho_buildingno, kbp.ho_res_asso_no, kbp.ho_houseno, kbp.ho_housename_en, ")
            .append("kbp.ho_housename_ml, kbp.ho_locality_en, kbp.ho_locality_ml, kbp.ho_villageid, kbp.ho_talukid, kbp.ho_districtid, kbp.ho_city_en, kbp.ho_city_ml, ")
            .append("kbp.ho_stateid, kbp.ho_poid, kbp.ho_pinno, kbp.ho_countryid, kbp.ward_id, kbp.oth_details_en, kbp.oth_details_ml, kbp.institution_type_id, ")
            .append("kbp.institution_id, kbp.auth_officer_id, kbp.auth_officer_desig_id, kbp.oth_auth_officer_name, kbp.oth_auth_officer_desig, kbp.informantsname_en, ")
            .append("kbp.informantsname_ml, kbp.informantsaddress_en, kbp.informantsaddress_ml, kbp.informants_mobileno, kbp.informants_aadhaar_no, ")
            .append("kbp.createdtime, kbp.createdby, kbp.lastmodifiedtime, kbp.lastmodifiedby,")
            .append("kbfi.firstname_en as father_fn, kbfi.firstname_ml as father_fn_ml, kbfi.middlename_en as father_mn, kbfi.middlename_ml  as father_mn_ml , ")
            .append("kbfi.lastname_en  as father_ln, kbfi.lastname_en  as father_ln_ml, kbfi.aadharno  as fa_aadh,")
            .append("kbfi.ot_passportno as fa_pass, kbfi.emailid as fa_email, kbfi.mobileno as fa_mob, kbfi.createdtime, kbfi.createdby, kbfi.lastmodifiedtime, kbfi.lastmodifiedby,")
            .append("kbfi.birthdtlid, kbmi.firstname_en  as mother_fn, kbmi.firstname_ml  as mother_fn_ml, kbmi.middlename_en as mother_mn, kbmi.middlename_ml as mother_mn_ml, kbmi.lastname_en as mother_ln, kbmi.lastname_ml as mother_ln_ml,")
            .append("kbmi.aadharno  as mo_aadh, kbmi.ot_passportno  as mo_pass, kbmi.emailid  as mo_email, kbmi.mobileno  as mo_mob, kbmi.createdtime, kbmi.createdby, kbmi.lastmodifiedtime, kbmi.lastmodifiedby,")
           .append("kbmi.birthdtlid,kperad.id as per_id,kperad.resdnce_addr_type as per_resdnce_addr_type,kperad.buildingno as per_buildingno,kperad.houseno as per_houseno,kperad.res_asso_no as per_res_asso_no,")
            .append("kperad.housename_en as per_housename_en,kperad.housename_ml as per_housename_ml,kperad.ot_address1_en as per_ot_address1_en,kperad.ot_address1_ml as per_ot_address1_ml,")
            .append("kperad.ot_address2_en as per_ot_address2_en,kperad.ot_address2_ml as per_ot_address2_ml,kperad.locality_en as per_locality_en,kperad.locality_ml as per_locality_ml,")
            .append("kperad.city_en as per_city_en,kperad.city_ml as per_city_ml,kperad.villageid as per_villageid,kperad.tenantid as per_tenantid,kperad.talukid as per_talukid,kperad.districtid as per_districtid,")
            .append("kperad.stateid as per_stateid,kperad.poid as per_poid,kperad.pinno as per_pinno,kperad.ot_state_region_province_en as per_ot_state_region_province_en,")
            .append("kperad.ot_state_region_province_ml as per_ot_state_region_province_ml,kperad.countryid as per_countryid,kperad.createdby as per_createdby,kperad.createdtime as per_createdtime,")
            .append("kperad.lastmodifiedby as per_lastmodifiedby,kperad.lastmodifiedtime as per_lastmodifiedtime,kperad.birthdtlid as per_birthdtlid,kperad.same_as_permanent as per_same_as_permanent,")
            .append("kperad.bio_adopt as per_bio_adopt,kperad.res_asso_no_ml as per_res_asso_no_ml,kperad.streetname_en as per_streetname_en,kperad.streetname_ml as per_streetname_ml,kpreadd.id as pres_id,")
            .append("kpreadd.resdnce_addr_type as pres_resdnce_addr_type,kpreadd.buildingno as pres_buildingno,kpreadd.houseno as pres_houseno,kpreadd.res_asso_no as pres_res_asso_no,kpreadd.housename_en as pres_housename_en,")
            .append("kpreadd.housename_ml as pres_housename_ml,kpreadd.ot_address1_en as pres_ot_address1_en,kpreadd.ot_address1_ml as pres_ot_address1_ml,kpreadd.ot_address2_en as pres_ot_address2_en,")
            .append("kpreadd.ot_address2_ml as pres_ot_address2_ml,kpreadd.locality_en as pres_locality_en,kpreadd.locality_ml as pres_locality_ml,kpreadd.city_en as pres_city_en,kpreadd.city_ml as pres_city_ml,")
            .append("kpreadd.villageid as pres_villageid,kpreadd.tenantid as pres_tenantid,kpreadd.talukid as pres_talukid,kpreadd.districtid as pres_districtid,kpreadd.stateid as pres_stateid,kpreadd.poid as pres_poid,")
            .append("kpreadd.pinno as pres_pinno,kpreadd.ot_state_region_province_en as pres_ot_state_region_province_en,kpreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,kpreadd.countryid as pres_countryid,")
            .append("kpreadd.createdby as pres_createdby,kpreadd.createdtime as pres_createdtime,kpreadd.lastmodifiedby as pres_lastmodifiedby,kpreadd.lastmodifiedtime as pres_lastmodifiedtime,")
            .append("kpreadd.birthdtlid as pres_birthdtlid,kpreadd.bio_adopt as pres_bio_adopt,kpreadd.res_asso_no_ml as pres_res_asso_no_ml,kpreadd.streetname_en as pres_streetname_en,")
            .append("kstat.weight_of_child, kstat.duration_of_pregnancy_in_week, kstat.nature_of_medical_attention, kstat.way_of_pregnancy,")
            .append("kstat.delivery_method, kstat.deliverytypeothers_en, kstat.deliverytypeothers_ml, kstat.religionid, kstat.father_nationalityid,")
            .append("kstat.father_educationid, kstat.father_education_subid, kstat.father_proffessionid, kstat.mother_educationid, kstat.mother_education_subid,")
            .append("kstat.mother_proffessionid, kstat.mother_nationalityid, kstat.mother_age_marriage, kstat.mother_age_delivery, kstat.mother_no_of_birth_given,")
            .append("kstat.mother_maritalstatusid, kstat.mother_unmarried, kstat.mother_res_lbid, kstat.mother_res_lb_code_id, kstat.mother_res_place_type_id,")
            .append("kstat.mother_res_lb_type_id, kstat.mother_res_district_id, kstat.mother_res_state_id, kstat.mother_res_country_id,")
            .append("kstat.mother_resdnce_addr_type, kstat.mother_resdnce_tenant, kstat.mother_resdnce_placetype, kstat.mother_resdnce_place_en,")
            .append("kstat.mother_resdnce_place_ml, kstat.mother_resdnce_lbtype, kstat.mother_resdnce_district, kstat.mother_resdnce_state,")
            .append("kstat.mother_resdnce_country, kstat.birthdtlid, kstat.createdby, kstat.createdtime, kstat.lastmodifiedtime, kstat.lastmodifiedby")
            .append(" FROM public.eg_register_birth_details krbd ")
            .append(" LEFT JOIN eg_register_birth_place kbp ON kbp.birthdtlid = krbd.id ")
            .append(" LEFT JOIN eg_register_birth_father_information kbfi ON kbfi.birthdtlid = krbd.id AND kbfi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_mother_information kbmi ON kbmi.birthdtlid = krbd.id AND kbmi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_permanent_address kperad ON kperad.birthdtlid = krbd.id AND kperad.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_present_address kpreadd ON kpreadd.birthdtlid = krbd.id AND kpreadd.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_statitical_information kstat ON kstat.birthdtlid = krbd.id")
            .toString();


    public String getRegBirthApplicationSearchQuery(@NotNull RegisterBirthSearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query=new StringBuilder(QUERY);
        addFilter("krbd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("krbd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilter("krbd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addDateRangeFilter("krbd.dateofreport", criteria.getFromDate(), criteria.getToDate(), query, preparedStmtValues);
        addDateRangeFilter("krbd.file_date", criteria.getFromDateReg(), criteria.getToDateReg(), query, preparedStmtValues);
        return query.toString();
    }


}
