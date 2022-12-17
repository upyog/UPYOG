package org.ksmart.birth.birthcorrection.repository.querybuilder;


import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class BirthCorrectionQueryBuilder extends BaseCorrectionQuery {
    private static final String QUERY = new StringBuilder()
            .append("SELECT ebd.id,	ebd.dateofreport,	ebd.dateofbirth,	ebd.timeofbirth,	ebd.am_pm,	ebd.firstname_en,	ebd.firstname_ml,	ebd.middlename_en,")
            .append("ebd.middlename_ml, ebd.lastname_en, ebd.lastname_ml, ebd.tenantid, ebd.gender, ebd.remarks_en, ebd.remarks_ml, ebd.aadharno,")
            .append("ebd.esign_user_code, ebd.esign_user_desig_code, ebd.is_adopted, ebd.is_abandoned, ebd.is_multiple_birth, ebd.is_father_info_missing,")
            .append("ebd.is_mother_info_missing, ebd.no_of_alive_birth, ebd.multiplebirthdetid, ebd.is_born_outside, ebd.ot_passportno, ebd.ot_dateofarrival,")
            .append("ebd.applicationtype, ebd.businessservice, ebd.workflowcode, ebd.fm_fileno, ebd.file_date, ebd.applicationno, ebd.registrationno,")
            .append("ebd.registration_date, ebd.action, ebd.status, ebd.createdtime, ebd.createdby, ebd.lastmodifiedtime, ebd.lastmodifiedby, ebfi.id,")
            .append("ebp.id, ebp.birthdtlid, ebp.placeofbirthid, ebp.hospitalid, ebp.vehicletypeid, ebp.vehicle_registration_no, ebp.vehicle_from_en, ebp.vehicle_to_en, ")
            .append("ebp.vehicle_from_ml, ebp.vehicle_to_ml, ebp.vehicle_other_en, ebp.vehicle_other_ml, ebp.vehicle_admit_hospital_en, ebp.vehicle_admit_hospital_ml, ")
            .append("ebp.public_place_id, ebp.ho_householder_en, ebp.ho_householder_ml, ebp.ho_buildingno, ebp.ho_res_asso_no, ebp.ho_houseno, ebp.ho_housename_en, ")
            .append("ebp.ho_housename_ml, ebp.ho_locality_en, ebp.ho_locality_ml, ebp.ho_villageid, ebp.ho_talukid, ebp.ho_districtid, ebp.ho_city_en, ebp.ho_city_ml, ")
            .append("ebp.ho_stateid, ebp.ho_poid, ebp.ho_pinno, ebp.ho_countryid, ebp.ward_id, ebp.oth_details_en, ebp.oth_details_ml, ebp.institution_type_id, ")
            .append("ebp.institution_id, ebp.auth_officer_id, ebp.auth_officer_desig_id, ebp.oth_auth_officer_name, ebp.oth_auth_officer_desig, ebp.informantsname_en, ")
            .append("ebp.informantsname_ml, ebp.informantsaddress_en, ebp.informantsaddress_ml, ebp.informants_mobileno, ebp.informants_aadhaar_no, ebp.is_born_outside, ")
            .append("ebp.createdtime, ebp.createdby, ebp.lastmodifiedtime, ebp.lastmodifiedby,")
            .append("ebfi.firstname_en, ebfi.firstname_ml, ebfi.middlename_en, ebfi.middlename_ml, ebfi.lastname_en, ebfi.lastname_ml, ebfi.aadharno,")
            .append("ebfi.ot_passportno, ebfi.emailid, ebfi.mobileno, ebfi.createdtime, ebfi.createdby, ebfi.lastmodifiedtime, ebfi.lastmodifiedby,")
            .append("ebfi.birthdtlid, ebmi.firstname_en, ebmi.firstname_ml, ebmi.middlename_en, ebmi.middlename_ml, ebmi.lastname_en, ebmi.lastname_ml,")
            .append("ebmi.aadharno, ebmi.ot_passportno, ebmi.emailid, ebmi.mobileno, ebmi.createdtime, ebmi.createdby, ebmi.lastmodifiedtime, ebmi.lastmodifiedby,")
            .append("ebmi.birthdtlid, eperad.resdnce_addr_type, eperad.buildingno, eperad.houseno, eperad.res_asso_no, eperad.housename_en, eperad.housename_ml,")
            .append("eperad.ot_address1_en, eperad.ot_address1_ml, eperad.ot_address2_en, eperad.ot_address2_ml, eperad.locality_en, eperad.locality_ml, eperad.city_en,")
            .append("eperad.city_ml, eperad.villageid, eperad.tenantid, eperad.talukid, eperad.districtid, eperad.stateid, eperad.poid, eperad.pinno,")
            .append("eperad.ot_state_region_province_en, eperad.ot_state_region_province_ml, eperad.countryid, eperad.createdby, eperad.createdtime,")
            .append("eperad.lastmodifiedby, eperad.lastmodifiedtime, eperad.birthdtlid, eperad.same_as_permanent, epreadd.buildingno, epreadd.houseno,")
            .append("epreadd.res_asso_no, epreadd.housename_en, epreadd.housename_ml, epreadd.ot_address1_en, epreadd.ot_address1_ml, epreadd.ot_address2_en,")
            .append("epreadd.ot_address2_ml, epreadd.locality_en, epreadd.locality_ml, epreadd.city_en, epreadd.city_ml, epreadd.villageid,")
            .append("epreadd.tenantid, epreadd.talukid, epreadd.districtid, epreadd.stateid, epreadd.poid, epreadd.pinno, epreadd.ot_state_region_province_en,")
            .append("epreadd.ot_state_region_province_ml, epreadd.countryid, epreadd.createdby, epreadd.createdtime, epreadd.lastmodifiedby, epreadd.lastmodifiedtime,")
            .append("epreadd.birthdtlid, estat.weight_of_child, estat.duration_of_pregnancy_in_week, estat.nature_of_medical_attention, estat.way_of_pregnancy,")
            .append("estat.delivery_method, estat.deliverytypeothers_en, estat.deliverytypeothers_ml, estat.religionid, estat.father_nationalityid,")
            .append("estat.father_educationid, estat.father_education_subid, estat.father_proffessionid, estat.mother_educationid, estat.mother_education_subid,")
            .append("estat.mother_proffessionid, estat.mother_nationalityid, estat.mother_age_marriage, estat.mother_age_delivery, estat.mother_no_of_birth_given,")
            .append("estat.mother_maritalstatusid, estat.mother_unmarried, estat.mother_res_lbid, estat.mother_res_lb_code_id, estat.mother_res_place_type_id,")
            .append("estat.mother_res_lb_type_id, estat.mother_res_district_id, estat.mother_res_state_id, estat.mother_res_country_id,")
            .append("estat.mother_resdnce_addr_type, estat.mother_resdnce_tenant, estat.mother_resdnce_placetype, estat.mother_resdnce_place_en,")
            .append("estat.mother_resdnce_place_ml, estat.mother_resdnce_lbtype, estat.mother_resdnce_district, estat.mother_resdnce_state,")
            .append("estat.mother_resdnce_country, estat.birthdtlid, estat.createdby, estat.createdtime, estat.lastmodifiedtime, estat.lastmodifiedby")
            .append(" FROM public.kl_birth_details ebd")
            .append(" LEFT JOIN kl_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN kl_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id")
            .append(" LEFT JOIN kl_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id")
            .append(" LEFT JOIN kl_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id")
            .append(" LEFT JOIN kl_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id")
            .append(" LEFT JOIN kl_birth_statitical_information estat ON estat.birthdtlid = ebd.id").toString();

    public String getBirthApplicationSearchQuery(@NotNull BirthApplicationSearchCriteria criteria,
                                                 @NotNull List<Object> preparedStmtValues, Boolean isCount) {
        StringBuilder query = new StringBuilder(QUERY);

        addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilter("ebd.applicationno", criteria.getApplicationNo(), query, preparedStmtValues);
        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);

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
