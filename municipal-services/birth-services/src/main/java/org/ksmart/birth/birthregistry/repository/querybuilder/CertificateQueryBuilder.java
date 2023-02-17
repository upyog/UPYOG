package org.ksmart.birth.birthregistry.repository.querybuilder;

import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
@Component
public class CertificateQueryBuilder extends BaseRegBuilder {
    private static final String QUERY=new StringBuilder().append("SELECT krbd.id, krbd.dateofreport, krbd.dateofbirth, krbd.firstname_en, krbd.firstname_ml, krbd.middlename_en, krbd.middlename_ml,")
            .append("krbd.lastname_en, krbd.lastname_ml, krbd.tenantid, krbd.gender, krbd.remarks_en, krbd.remarks_ml, krbd.aadharno, krbd.esign_user_code,")
            .append("krbd.esign_user_desig_code, krbd.is_adopted,  krbd.registrationno, krbd.registration_date, krbd.ack_no, kbfi.firstname_en, kbfi.firstname_ml,")
            .append("kbmi.firstname_en, kbmi.firstname_ml, kperad.housename_no_en as per_housename_en, kperad.locality_en as per_locality_en,")
            .append("kpread.housename_no_ml as pres_housename_no_ml,kpread.ot_address1_en as pres_ot_address1_en,")
            .append("kpread.ot_address1_ml as pres_ot_address1_ml,kpread.ot_address2_en as pres_ot_address2_en,kpread.ot_address2_ml as pres_ot_address2_ml,")
            .append("kpread.districtid as pres_districtid,kpread.stateid as pres_stateid,kpread.poid as pres_poid,kpread.pinno as pres_pinno,kpread.ot_state_region_province_en as pres_ot_state_region_province_en,")
            .append("kpread.ot_state_region_province_ml as pres_ot_state_region_province_ml,kpread.countryid as pres_countryid,")
            .append("kpread.ot_zipcode as pres_ot_zipcode, kpread.locality_en as pres_locality_en, kpread.street_name_en as pres_street_name_en, kpread.locality_ml as pres_locality_ml, ")
            .append("kpread.street_name_ml as pres_street_name_ml,kperad.housename_no_ml as per_housename_no_ml,kperad.ot_address1_en as per_ot_address1_en,")
            .append("kperad.housename_no_ml as per_housename_no_ml,kperad.ot_address1_en as per_ot_address1_en,")
            .append("kperad.ot_address1_ml as per_ot_address1_ml,kperad.ot_address2_en as per_ot_address2_en,kperad.ot_address2_ml as per_ot_address2_ml,")
            .append("kperad.districtid as per_districtid,kperad.stateid as per_stateid,kperad.poid as per_poid,kperad.pinno as per_pinno,kperad.ot_state_region_province_en as per_ot_state_region_province_en,")
            .append("kperad.ot_state_region_province_ml as per_ot_state_region_province_ml,kperad.countryid as per_countryid,")
            .append("kperad.ot_zipcode as per_ot_zipcode, kperad.locality_en as per_locality_en,")
            .append("kperad.street_name_en as per_street_name_en, kperad.locality_ml as per_locality_ml, kperad.street_name_ml as per_street_name_ml,")
            .append("kbp.placeofbirthid, kbp.hospitalid, kbp.public_place_id,")
            .append("kbp.institution_type_id, kbp.institution_id, kbp.vehicletypeid, kbp.vehicle_registration_no, kbp.vehicle_from_en, kbp.vehicle_to_en,")
            .append("kbp.vehicle_from_ml, kbp.vehicle_to_ml,kbp.vehicle_other_en, kbp.vehicle_other_ml, kbp.vehicle_admit_hospital_en, kbp.vehicle_admit_hospital_ml,")
            .append("kbp.ho_householder_en, kbp.ho_householder_ml, kbp.ho_locality_en, kbp.ho_locality_ml, kbp.ho_street_name_en,")
            .append("kbp.ho_street_name_ml, kbp.ho_housename_en, kbp.ho_housename_ml,kbp.ho_districtid, kbp.ho_stateid, kbp.ho_poid, kbp.ho_pinno, kbp.ho_countryid,")
            .append("kbp.oth_details_en, kbp.oth_details_ml, kbp.vehicle_desc, kbp.public_place_desc")
            .append(" FROM public.eg_register_birth_details krbd LEFT JOIN eg_register_birth_place kbp ON kbp.birthdtlid = krbd.id")
            .append(" LEFT JOIN eg_register_birth_father_information kbfi ON kbfi.birthdtlid = krbd.id AND kbfi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_mother_information kbmi ON kbmi.birthdtlid = krbd.id AND kbmi.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_permanent_address kperad ON kperad.birthdtlid = krbd.id AND kperad.bio_adopt='BIOLOGICAL'")
            .append(" LEFT JOIN eg_register_birth_present_address kpread ON kpread.birthdtlid = krbd.id AND kpread.bio_adopt='BIOLOGICAL'").toString();

    public String getRegBirthApplicationSearchQuery(@NotNull RegisterBirthSearchCriteria criteria, @NotNull List<Object> preparedStmtValues, Boolean isCount) {
        StringBuilder query=new StringBuilder(QUERY);
        addFilter("krbd.id", criteria.getId(), query, preparedStmtValues);
        addFilter("krbd.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addLikeFilter("kbmi.firstname_en", criteria.getNameOfMother(), query, preparedStmtValues);
        addFilter("krbd.gender", criteria.getGender(), query, preparedStmtValues);
        addDateToLongFilter("krbd.dateofbirth", criteria.getDob(), query, preparedStmtValues);
        addFilter("krbd.registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addDateRangeFilter("krbd.dateofreport", criteria.getFromDate(), criteria.getToDate(), query, preparedStmtValues);
        addDateRangeFilter("krbd.file_date", criteria.getFromDateReg(), criteria.getToDateReg(), query, preparedStmtValues);
        return query.toString();
    }
}
