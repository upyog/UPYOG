package org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthStatiticalInformation;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterStatisticsRowMapperForApp {
    default RegisterBirthStatiticalInformation getRegBirthStatisticalInfo(ResultSet rs) throws SQLException {
        return RegisterBirthStatiticalInformation.builder()
                .id(rs.getString("stat_id"))
                .weightOfChild(rs.getLong("stat_weight_of_child"))
                .durationOfPregnancyInWeek(rs.getInt("stat_duration_of_pregnancy_in_week"))
                .natureOfMedicalAttention(rs.getString("stat_nature_of_medical_attention"))
              //  .wayOfPregnancy(rs.getString("stat_way_of_pregnancy"))
                .deliveryMethod(rs.getString("stat_delivery_method"))
               // .deliveryTypeOthersEn(rs.getString("stat_deliverytypeothers_en"))
               // .deliveryTypeOthersMl(rs.getString("stat_deliverytypeothers_ml"))
                .religionId(rs.getString("stat_religionid"))
                .fatherNationalityId(rs.getString("stat_father_nationalityid"))
                .fatherEducationId(rs.getString("stat_father_educationid"))
                //.fatherEducationSubId(rs.getString("stat_father_education_subid"))
                .fatherProffessionId(rs.getString("stat_father_proffessionid"))
                .motherEducationId(rs.getString("stat_mother_educationid"))
                //.motherEducationSubId(rs.getString("stat_mother_education_subid"))
                .motherProffessionId(rs.getString("stat_mother_proffessionid"))
                .motherNationalityId(rs.getString("stat_mother_nationalityid"))
                .motherAgeMarriage(rs.getInt("stat_mother_age_marriage"))
                .motherAgeDelivery(rs.getInt("stat_mother_age_delivery"))
                .motherNoOfBirthGiven(rs.getInt("stat_mother_no_of_birth_given"))
                .motherMaritalStatusId(rs.getString("stat_mother_maritalstatusid"))
                .motherUnmarried(rs.getInt("stat_mother_unmarried"))
                .motherResLbId(rs.getInt("stat_mother_res_lbid"))
                .motherResLbCode(rs.getInt("stat_mother_res_lb_code_id"))
                .motherResPlaceTypeId(rs.getInt("stat_mother_res_place_type_id"))
                .motherResLbTypeId(rs.getInt("stat_mother_res_lb_type_id"))
                .motherResDistrictId(rs.getInt("stat_mother_res_district_id"))
                .motherResStateId(rs.getInt("stat_mother_res_state_id"))
                .motherResCountryId(rs.getInt("stat_mother_res_country_id"))
                .motherResdnceAddrType(rs.getString("stat_mother_resdnce_addr_type"))
                .motherResdnceTenentId(rs.getString("stat_mother_resdnce_tenant"))
                .motherResdncePlaceType(rs.getString("stat_mother_resdnce_placetype"))
                .motherResdncePlaceEn(rs.getString("stat_mother_resdnce_place_en"))
                .motherResdncePlaceMl(rs.getString("stat_mother_resdnce_place_ml"))
                .motherResdnceLbType(rs.getString("stat_mother_resdnce_lbtype"))
                .motherResdnceDistrictId(rs.getString("stat_mother_resdnce_district"))
                .motherResdnceStateId(rs.getString("stat_mother_resdnce_state"))
                .motherResdnceCountryId(rs.getString("stat_mother_resdnce_country"))
               // .birthDtlId(rs.getString("stat_birthdtlid"))
                .build();
    }
}
