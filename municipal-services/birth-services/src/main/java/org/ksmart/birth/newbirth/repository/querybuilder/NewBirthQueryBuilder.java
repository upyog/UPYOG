package org.ksmart.birth.newbirth.repository.querybuilder;

import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.web.model.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class NewBirthQueryBuilder extends NewBaseBirthQuery {
    @Autowired
    CommonQueryBuilder commonQueryBuilder;
    private static final String QUERY = new StringBuilder().append("SELECt ebd.id as ba_id,ebd.dateofreport as ba_dateofreport,ebd.dateofbirth as ba_dateofbirth,ebd.timeofbirth as ba_timeofbirth,ebd.am_pm as ba_am_pm,ebd.firstname_en as ba_firstname_en,")
            .append("ebd.firstname_ml as ba_firstname_ml,ebd.middlename_en as ba_middlename_en,ebd.middlename_ml as ba_middlename_ml,ebd.lastname_en as ba_lastname_en,ebd.lastname_ml as ba_lastname_ml,")
            .append("ebd.tenantid as ba_tenantid,ebd.gender as ba_gender,ebd.remarks_en as ba_remarks_en,ebd.remarks_ml as ba_remarks_ml,ebd.aadharno as ba_aadharno,ebd.esign_user_code as ba_esign_user_code,")
            .append("ebd.esign_user_desig_code as ba_esign_user_desig_code,ebd.is_father_info_missing as ba_is_father_info_missing,ebd.is_mother_info_missing as ba_is_mother_info_missing,")
            .append("ebd.applicationtype as ba_applicationtype,ebd.businessservice as ba_businessservice,ebd.workflowcode as ba_workflowcode,ebd.fm_fileno as ba_fm_fileno,")
            .append("ebd.file_date as ba_file_date,ebd.file_status as ba_file_status,ebd.applicationno as ba_applicationno,ebd.registrationno as ba_registrationno,")
            .append("ebd.registration_date as ba_registration_date,ebd.action as ba_action,ebd.status as ba_status,ebd.createdtime,ebd.createdby,ebd.lastmodifiedtime,ebd.lastmodifiedby").toString();



//            .append("ebmi.id as mo_id,ebmi.firstname_en as mo_firstname_en,ebmi.firstname_ml as mo_firstname_ml,ebmi.aadharno as mo_aadharno,ebmi.birthdtlid as mo_birthdtlid,ebmi.bio_adopt as mo_bio_adopt,")
//
//            .append("eperad.id as per_id,eperad.housename_no_en as per_housename_no_en,eperad.housename_no_ml as per_housename_no_ml,eperad.ot_address1_en as per_ot_address1_en,")
//            .append("eperad.ot_address1_ml as per_ot_address1_ml,eperad.ot_address2_en as per_ot_address2_en,")
//            .append("eperad.ot_address2_ml as per_ot_address2_ml,eperad.villageid as per_villageid,eperad.tenantid as per_tenantid,eperad.talukid as per_talukid,")
//            .append("eperad.districtid as per_districtid,eperad.stateid as per_stateid,eperad.poid as per_poid,eperad.pinno as per_pinno,eperad.ot_state_region_province_en as per_ot_state_region_province_en,")
//            .append("eperad.ot_state_region_province_ml as per_ot_state_region_province_ml,eperad.countryid as per_countryid,eperad.createdby,")
//            .append("eperad.birthdtlid as per_birthdtlid,eperad.same_as_present as per_same_as_present,eperad.bio_adopt as per_bio_adopt,eperad.res_asso_no_ml as per_res_asso_no_ml,")
//            .append("eperad.taluk_name as per_taluk_name, eperad.village_name as per_village_name, eperad.ward_code as per_ward_code,eperad.ot_zipcode as per_ot_zipcode, eperad.locality_en as per_locality_en,")
//            .append("eperad.street_name_en as per_street_name_en, eperad.locality_ml as per_locality_ml, eperad.street_name_ml as per_street_name_ml,")
//
//            .append("ebp.id as pla_id,ebp.birthdtlid as pla_birthdtlid,ebp.placeofbirthid as pla_placeofbirthid,ebp.hospitalid as pla_hospitalid,ebp.public_place_id as pla_public_place_id,ebp.institution_type_id as pla_institution_type_id,")
//            .append("ebp.institution_id as pla_institution_id,ebp.vehicletypeid as pla_vehicletypeid,ebp.vehicle_registration_no as pla_vehicle_registration_no,ebp.vehicle_from_en as pla_vehicle_from_en,")
//            .append("ebp.vehicle_to_en as pla_vehicle_to_en,ebp.vehicle_from_ml as pla_vehicle_from_ml,ebp.vehicle_to_ml as pla_vehicle_to_ml,ebp.vehicle_admit_hospital_en as pla_vehicle_admit_hospital_en,")
//            .append("ebp.ho_householder_en as pla_ho_householder_en,ebp.ho_locality_en as pla_ho_locality_en,ebp.ho_locality_ml as pla_ho_locality_ml,ebp.ho_street_name_en as pla_ho_street_name_en,")
//            .append("ebp.ho_street_name_ml as pla_ho_street_name_ml,ebp.ho_housename_en as pla_ho_housename_en,ebp.ho_housename_ml as pla_ho_housename_ml,ebp.ho_villageid as pla_ho_villageid,")
//            .append("ebp.ho_talukid as pla_ho_talukid,ebp.ho_districtid as pla_ho_districtid,ebp.ho_stateid as pla_ho_stateid,ebp.ho_poid as pla_ho_poid,ebp.ho_pinno as pla_ho_pinno,")
//            .append("ebp.ho_countryid as pla_ho_countryid,ebp.ward_id as pla_ward_id,ebp.auth_officer_id as pla_auth_officer_id,ebp.auth_officer_desig_id as pla_auth_officer_desig_id,")
//            .append("ebp.oth_auth_officer_name as pla_oth_auth_officer_name,ebp.oth_auth_officer_desig as pla_oth_auth_officer_desig,ebp.informantsaddress_en as pla_informantsaddress_en,")
//            .append("ebp.informants_mobileno as pla_informants_mobileno,ebp.informants_aadhaar_no as pla_informants_aadhaar_no,ebp.is_inform_declare as pla_is_inform_declare,")
//            .append("ebp.vehicle_haltplace_en as pla_vehicle_haltplace_en,ebp.vehicle_hospitalid as pla_vehicle_hospitalid,ebp.createdby as pla_createdby,ebp.createdtime as pla_createdtime,")
//            .append("ebp.lastmodifiedby as pla_lastmodifiedby,ebp.lastmodifiedtime as pla_lastmodifiedtime,ebp.vehicle_haltplace_ml as pla_vehicle_haltplace_ml,ebp.vehicle_desc as pla_vehicle_desc,")
//            .append("ebp.public_place_desc as pla_public_place_desc,ebp.public_locality_en as pla_public_locality_en,ebp.public_locality_ml as pla_public_locality_ml,ebp.public_street_name_en as pla_public_street_name_en,")
//            .append("ebp.public_street_name_ml as pla_public_street_name_ml,")
//
//            .append("epreadd.id as pres_id,epreadd.housename_no_en as pres_housename_no_en,epreadd.housename_no_ml as pres_housename_no_ml,epreadd.ot_address1_en as pres_ot_address1_en,")
//            .append("epreadd.ot_address1_ml as pres_ot_address1_ml,epreadd.ot_address2_en as pres_ot_address2_en,epreadd.ot_address2_ml as pres_ot_address2_ml,")
//            .append("epreadd.villageid as pres_villageid,epreadd.tenantid as pres_tenantid,epreadd.talukid as pres_talukid,")
//            .append("epreadd.districtid as pres_districtid,epreadd.stateid as pres_stateid,epreadd.poid as pres_poid,epreadd.pinno as pres_pinno,epreadd.ot_state_region_province_en as pres_ot_state_region_province_en,")
//            .append("epreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,epreadd.countryid as pres_countryid,epreadd.createdby,epreadd.createdtime,")
//            .append("epreadd.lastmodifiedby,epreadd.lastmodifiedtime ,epreadd.birthdtlid as pres_birthdtlid,epreadd.bio_adopt as pres_bio_adopt,epreadd.res_asso_no_ml as pres_res_asso_no_ml,")
//            .append("epreadd.taluk_name as pres_taluk_name, epreadd.village_name as pres_village_name, epreadd.ward_code as pres_ward_code, epreadd.doorno as pres_doorno, epreadd.subno as pres_subno , ")
//            .append("epreadd.ot_zipcode as pres_ot_zipcode, epreadd.locality_en as pres_locality_en,")
//            .append("epreadd.street_name_en as pres_street_name_en, epreadd.locality_ml as pres_locality_ml, epreadd.street_name_ml as pres_street_name_ml, ")
//
//            .append("estat.id as stat_id,estat.weight_of_child as stat_weight_of_child,estat.duration_of_pregnancy_in_week as stat_duration_of_pregnancy_in_week,estat.nature_of_medical_attention as stat_nature_of_medical_attention,")
//            .append("estat.delivery_method as stat_delivery_method,estat.religionid as stat_religionid,estat.father_nationalityid as stat_father_nationalityid,estat.father_educationid as stat_father_educationid,")
//            .append("estat.father_proffessionid as stat_father_proffessionid,estat.mother_educationid as stat_mother_educationid,estat.mother_proffessionid as stat_mother_proffessionid," )
//            .append("estat.mother_nationalityid as stat_mother_nationalityid,estat.mother_age_marriage as stat_mother_age_marriage,estat.mother_age_delivery as stat_mother_age_delivery," )
//            .append("estat.mother_no_of_birth_given as stat_mother_no_of_birth_given,estat.mother_maritalstatusid as stat_mother_maritalstatusid,estat.mother_unmarried as stat_mother_unmarried,")
//            .append("estat.mother_res_lbid as stat_mother_res_lbid,estat.mother_res_lb_code_id as stat_mother_res_lb_code_id,estat.mother_res_place_type_id as stat_mother_res_place_type_id,")
//            .append("estat.mother_res_lb_type_id as stat_mother_res_lb_type_id,estat.mother_res_district_id as stat_mother_res_district_id,estat.mother_res_state_id as stat_mother_res_state_id," )
//            .append("estat.mother_res_country_id as stat_mother_res_country_id,estat.mother_resdnce_addr_type as stat_mother_resdnce_addr_type,estat.mother_resdnce_tenant as stat_mother_resdnce_tenant,")
//            .append("estat.mother_resdnce_placetype as stat_mother_resdnce_placetype,estat.mother_resdnce_place_en as stat_mother_resdnce_place_en,estat.mother_resdnce_place_ml as stat_mother_resdnce_place_ml,")
//            .append("estat.mother_resdnce_lbtype as stat_mother_resdnce_lbtype,estat.mother_resdnce_district as stat_mother_resdnce_district,estat.mother_resdnce_state as stat_mother_resdnce_state," )
//            .append("estat.mother_resdnce_country as stat_mother_resdnce_country,estat.birthdtlid as stat_birthdtlid,estat.mother_order_of_cur_delivery as stat_mother_order_of_cur_delivery,")
//            .append("estat.mother_order_cur_child as stat_mother_order_cur_child,estat.mother_res_no_of_years as stat_mother_res_no_of_years,")
//
//            .append("ini.id as ini_id,ini.birthdtlid as ini_birthdtlid,ini.initiator_name as ini_initiator_name,ini.initiator_institution as ini_initiator_institution,")
//            .append("ini.initiator_inst_desig as ini_initiator_inst_desig,ini.relation as ini_relation,ini.initiator_address as ini_initiator_address,")
//            .append("ini.is_declared as ini_is_declared,ini.declaration_id as ini_declaration_id,ini.aadharno as ini_aadharno,ini.mobileno as ini_mobileno,ini.is_care_taker as ini_is_care_taker,ini.is_esigned as ini_is_esigned")

    private static final String QUERYCONDITION = new StringBuilder().append(" FROM public.eg_birth_details ebd LEFT JOIN eg_birth_place ebp ON ebp.birthdtlid = ebd.id LEFT JOIN eg_birth_father_information ebfi ON ebfi.birthdtlid = ebd.id AND ebfi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_mother_information ebmi ON ebmi.birthdtlid = ebd.id AND ebmi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_permanent_address eperad ON eperad.birthdtlid = ebd.id AND eperad.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_present_address epreadd ON epreadd.birthdtlid = ebd.id AND epreadd.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_birth_statitical_information estat ON estat.birthdtlid = ebd.id")
            .append(" LEFT JOIN eg_birth_initiator ini ON ini.birthdtlid = ebd.id").toString();

    public String getNewBirthApplicationSearchQuery(@NotNull SearchCriteria criteria,
                                                 @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);
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
                .append(QUERYCONDITION).toString();

        StringBuilder orderBy = new StringBuilder();
        addFilter("ebd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("ebd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilter("ebd.applicationno", criteria.getApplicationNumber(), query, preparedStmtValues);
        addFilter("ebd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addFilter("ebd.fm_fileno", criteria.getFileCode(), query, preparedStmtValues);
        addFilter("ebp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
        addFilter("ebp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
        addFilter("ebp.ebp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
        addFilter("eebd.gender", criteria.getGender(), query, preparedStmtValues);
        addDateRangeFilter("ebd.dateofreport", criteria.getFromDate(),  criteria.getToDate(), query, preparedStmtValues);
        addDateRangeFilter("ebd.dateofbirth",  criteria.getDateOfBirthFrom(), criteria.getDateOfBirthTo(),query, preparedStmtValues);
        addDateRangeFilter("ebd.fm_fileno",  criteria.getFromDateFile(), criteria.getToDateFile(), query, preparedStmtValues);

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



        addOrderToQuery(orderBy, query);
        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
        return query.toString();
    }
    public String getNextIDQuery() {
        StringBuilder query = new StringBuilder("select fn_next_id(?,?,?,?)");
        return query.toString();
    }

}
