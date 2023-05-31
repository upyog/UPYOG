package org.ksmart.birth.birthregistry.repository.querybuilder;


import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.NOT_FOUND;

@Component
public class RegisterQueryBuilder extends BaseRegBuilder {
    private static final String QUERY=new StringBuilder().append("SELECT krbd.id,krbd.dateofreport,krbd.dateofbirth,krbd.timeofbirth,krbd.am_pm,krbd.firstname_en,krbd.firstname_ml,krbd.middlename_en,")
            .append("krbd.middlename_ml,krbd.lastname_en,krbd.lastname_ml,krbd.tenantid,krbd.gender,krbd.remarks_en,krbd.remarks_ml,krbd.aadharno, krbd.ack_no,")
            .append("krbd.createdtime,krbd.createdby,krbd.lastmodifiedtime,krbd.lastmodifiedby,krbd.esign_user_code,krbd.esign_user_desig_code,krbd.is_adopted,")
            .append("krbd.is_abandoned,krbd.is_multiple_birth,krbd.is_father_info_missing,krbd.is_mother_info_missing,krbd.no_of_alive_birth,krbd.multiplebirthdetid,")
            .append("krbd.ot_passportno,krbd.registrationno,krbd.registration_status,krbd.registration_date,krbd.is_born_outside,krbd.ot_dateofarrival, krbd.ack_no,")
            .append("krbd.is_migrated, krbd.migrated_date, krbd.applicationid, krbd.applicationtype, krbd.migrated_from, ")

            //Birthplace
            .append("kbfi.id as pla_id,kbp.id,kbp.birthdtlid,kbp.placeofbirthid,kbp.hospitalid,kbp.public_place_id,kbp.institution_type_id,kbp.institution_id,kbp.vehicletypeid,kbp.vehicle_registration_no,kbp.vehicle_from_en," )
            .append("kbp.vehicle_to_en,kbp.vehicle_from_ml,kbp.vehicle_to_ml,kbp.vehicle_other_en,kbp.vehicle_other_ml,kbp.vehicle_admit_hospital_en,kbp.vehicle_admit_hospital_ml,kbp.ho_householder_en,")
            .append("kbp.ho_householder_ml,kbp.ho_buildingno,kbp.ho_houseno,kbp.ho_res_asso_no,kbp.ho_res_asso_no_ml,kbp.ho_locality_en,kbp.ho_locality_ml,kbp.ho_street_name_en,kbp.ho_street_name_ml,")
            .append("kbp.ho_doorno,kbp.ho_subno,kbp.ho_housename_en,kbp.ho_housename_ml,kbp.ho_villageid,kbp.ho_talukid,kbp.ho_districtid,kbp.ho_stateid,kbp.ho_poid,kbp.ho_pinno,kbp.ho_countryid,")
            .append("kbp.ward_id,kbp.oth_details_en,kbp.oth_details_ml,kbp.auth_officer_id,kbp.auth_officer_desig_id,kbp.oth_auth_officer_name,kbp.oth_auth_officer_desig,kbp.informantsname_en,")
            .append("kbp.informantsname_ml,kbp.informantsaddress_en,kbp.informantsaddress_ml,kbp.informants_mobileno,kbp.informants_aadhaar_no,kbp.is_born_outside,kbp.vehicle_haltplace_en,kbp.vehicle_hospitalid,")
            .append("kbp.informant_addressline2,kbp.createdby,kbp.createdtime,kbp.lastmodifiedby,kbp.lastmodifiedtime,kbp.mig_chvackno,kbp.vehicle_haltplace_ml,kbp.vehicle_desc,kbp.public_place_desc,")
            .append("kbp.public_locality_en, kbp.public_locality_ml, kbp.public_street_name_en, kbp.public_street_name_ml, kbp.ot_birth_place_en, kbp.ot_birth_place_ml, kbp.ot_address1_en, kbp.ot_address1_ml," )
            .append(" kbp.ot_address2_en,kbp.ot_address2_ml, kbp.ot_state_region_province_en, kbp.ot_state_region_province_ml, kbp.ot_zipcode, kbp.ot_country, kbp.ot_town_village_en, kbp.ot_town_village_ml,")

            // Father Information
            .append("kbfi.firstname_en as father_fn, kbfi.firstname_ml as father_fn_ml, kbfi.aadharno  as fa_aadh,kbfi.ot_passportno as fa_pass, ")

            //Mother Information
            .append(" kbmi.firstname_en  as mother_fn, kbmi.firstname_ml  as mother_fn_ml,kbmi.aadharno  as mo_aadh, kbmi.ot_passportno  as mo_pass,")

            // Permananent Address
            .append("kperad.id as per_id,kperad.housename_no_en as per_housename_no_en,kperad.housename_no_ml as per_housename_no_ml, kperad.res_asso_no as per_res_asso_no, kperad.res_asso_no_ml as per_res_asso_no_ml,")
            .append("kperad.ot_address1_en as per_ot_address1_en,kperad.ot_address1_ml as per_ot_address1_ml,")
            .append("kperad.ot_address2_en as per_ot_address2_en,kperad.ot_address2_ml as per_ot_address2_ml,kperad.ot_state_region_province_en as per_ot_state_region_province_en,")
            .append("kperad.ot_state_region_province_ml as per_ot_state_region_province_ml,kperad.ot_zipcode as per_ot_zipcode,kperad.villageid as per_villageid,kperad.village_name as per_village_name,")
            .append("kperad.tenantid as per_tenantid,kperad.talukid as per_talukid,kperad.taluk_name as per_taluk_name,kperad.locality_en as per_locality_en,kperad.locality_ml as per_locality_ml,")
            .append("kperad.street_name_en as per_street_name_en,kperad.street_name_ml as per_street_name_ml,kperad.districtid as per_districtid,kperad.stateid as per_stateid,kperad.poid as per_poid,")
            .append("kperad.pinno as per_pinno,kperad.countryid as per_countryid,kperad.birthdtlid as per_birthdtlid,kperad.bio_adopt as per_bio_adopt,kperad.same_as_present as per_same_as_present,")
            .append("kperad.family_emailid as per_family_emailid,kperad.family_mobileno as per_family_mobileno,kperad.postoffice_en as per_postoffice_en,kperad.postoffice_ml as per_postoffice_ml,")

            //Present Address
            .append("kpreadd.id as pres_id,kpreadd.housename_no_en as pres_housename_no_en,kpreadd.housename_no_ml as pres_housename_no_ml,kpreadd.res_asso_no as pres_res_asso_no, kpreadd.res_asso_no_ml as pres_res_asso_no_ml,kpreadd.ot_address1_en as pres_ot_address1_en,")
            .append("kpreadd.ot_address1_ml as pres_ot_address1_ml,kpreadd.ot_address2_en as pres_ot_address2_en,kpreadd.ot_address2_ml as pres_ot_address2_ml,")
            .append("kpreadd.ot_state_region_province_en as pres_ot_state_region_province_en,kpreadd.ot_state_region_province_ml as pres_ot_state_region_province_ml,kpreadd.ot_zipcode as pres_ot_zipcode,")
            .append("kpreadd.villageid as pres_villageid,kpreadd.village_name as pres_village_name,kpreadd.tenantid as pres_tenantid,kpreadd.talukid as pres_talukid,kpreadd.taluk_name as pres_taluk_name,")
            .append("kpreadd.locality_en as pres_locality_en,kpreadd.locality_ml as pres_locality_ml,kpreadd.street_name_en as pres_street_name_en,kpreadd.street_name_ml as pres_street_name_ml,")
            .append("kpreadd.districtid as pres_districtid,kpreadd.stateid as pres_stateid,kpreadd.poid as pres_poid,kpreadd.pinno as pres_pinno,kpreadd.postoffice_en as pres_postoffice_en,")
            .append("kpreadd.postoffice_ml as pres_postoffice_ml,kpreadd.countryid as pres_countryid,kpreadd.birthdtlid as pres_birthdtlid,kpreadd.bio_adopt as pres_bio_adopt")

            .append(" FROM public.eg_register_birth_details krbd ")
            .append(" LEFT JOIN eg_register_birth_place kbp ON kbp.birthdtlid = krbd.id ")
            .append(" LEFT JOIN eg_register_birth_father_information kbfi ON kbfi.birthdtlid = krbd.id ")
            .append(" LEFT JOIN eg_register_birth_mother_information kbmi ON kbmi.birthdtlid = krbd.id")
            .append(" LEFT JOIN eg_register_birth_permanent_address kperad ON kperad.birthdtlid = krbd.id")
            .append(" LEFT JOIN eg_register_birth_present_address kpreadd ON kpreadd.birthdtlid = krbd.id ")
            //.append(" LEFT JOIN eg_register_birth_statitical_information kstat ON kstat.birthdtlid = krbd.id")
            .toString();


    public String getRegBirthApplicationSearchQuery(@NotNull RegisterBirthSearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {
        StringBuilder query = new StringBuilder(QUERY);
        StringBuilder orderBy = new StringBuilder();
        addFilter("krbd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("krbd.ack_no", criteria.getApplicationNumber(), query, preparedStmtValues);
        addFilter("krbd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addLikeFilter("LOWER(kbmi.firstname_en)", criteria.getNameOfMother(), query, preparedStmtValues);
        addFilter("krbd.gender", criteria.getGender(), query, preparedStmtValues);
        addLongFilter("krbd.dateofbirth", criteria.getBirthDate(), query, preparedStmtValues);
        addLongFilter("krbd.registration_date", criteria.getRegistrationDate(), query, preparedStmtValues);
        addFilter("krbd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addDateRangeFilter("krbd.dateofreport", criteria.getFromDate(), criteria.getToDate(), query, preparedStmtValues);
        addDateRangeFilter("krbd.file_date", criteria.getFromDateReg(), criteria.getToDateReg(), query, preparedStmtValues);
        addFilter("kbp.hospitalid", criteria.getHospitalId(), query, preparedStmtValues);
        addFilter("kbp.institution_id", criteria.getInstitutionId(), query, preparedStmtValues);
        addFilter("kbp.ward_id", criteria.getWardCode(), query, preparedStmtValues);
        addLikeFilter("LOWER(krbd.firstname_en)", criteria.getChildName(), query, preparedStmtValues);
        addLikeFilter("LOWER(kbfi.firstname_en)", criteria.getNameOfFather(), query, preparedStmtValues);

        if(criteria.getBirthDate() != null){
            addDateRangeFilter("krbd.dateofbirth", criteria.getBirthDate(), criteria.getBirthDate()+(1*24*60*60*1000), query, preparedStmtValues);
        }

        if (StringUtils.isEmpty(criteria.getSortBy()))
            addOrderByColumns(" krbd.createdtime",null, orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.birthDate)
            addOrderByColumns("krbd.dateofbirth",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.registrationDate)
            addOrderByColumns("krbd.registration_date",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.applicationNumber)
            addOrderByColumns("krbd.ack_no",criteria.getSortOrder(),orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.nameOfMother)
            addOrderByColumns("kbmi.firstname_en",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.gender)
            addOrderByColumns("krbd.gender",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.registrationNo)
            addOrderByColumns("krbd.registrationno",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.hospitalId)
            addOrderByColumns("kbp.hospitalid",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.institutionId)
            addOrderByColumns("kbp.institution_id",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.wardCode)
            addOrderByColumns("kbp.ward_id",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.tenantId)
            addOrderByColumns("krbd.tenantid",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.childName)
            addOrderByColumns("krbd.firstname_en",criteria.getSortOrder(), orderBy);
        else if (criteria.getSortBy() == RegisterBirthSearchCriteria.SortBy.nameOfFather)
            addOrderByColumns("kbfi.firstname_en",criteria.getSortOrder(), orderBy);


        if(preparedStmtValues.size() > 0) {
            addOrderToQuery(orderBy, query);
            addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
            return query.toString();
        } else{
            throw new CustomException(NOT_FOUND.getCode(),
                    "Criteria not given for the search.");
        }
    }
}
