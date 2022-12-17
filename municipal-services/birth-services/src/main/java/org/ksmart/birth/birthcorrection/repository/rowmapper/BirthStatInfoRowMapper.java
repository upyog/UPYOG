package org.ksmart.birth.birthcorrection.repository.rowmapper;

import org.ksmart.birth.crbirth.model.BirthStatisticalInformation;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthStatInfoRowMapper {
        default BirthStatisticalInformation getBirthStatisticalInfo(ResultSet rs) throws SQLException {
            return BirthStatisticalInformation.builder()
                    .id(rs.getString("id"))
                    .weightOfChild(Long.valueOf(rs.getString("weight_of_child")))
                    .durationOfPregnancyInWeek(Integer.valueOf(rs.getString("duration_of_pregnancy_in_week")))
                    .natureOfMedicalAttention(rs.getString("nature_of_medical_attention"))
                    .wayOfPregnancy(rs.getString("way_of_pregnancy"))
                    .deliveryMethod(rs.getString("delivery_method"))
                    .deliveryTypeOthersEn(rs.getString("deliverytypeothers_en"))
                    .deliveryTypeOthersMl(rs.getString("deliverytypeothers_ml"))
                    .religionId(rs.getString("religionid"))
                    .fatherNationalityId(rs.getString("father_nationalityid"))
                    .fatherEducationId(rs.getString("father_educationid"))
                    .fatherEducationSubId(rs.getString("father_education_subid"))
                    .fatherProffessionId(rs.getString("father_proffessionid"))
                    .motherEducationId(rs.getString("mother_educationid"))
                    .motherEducationSubId(rs.getString("mother_education_subid"))
                    .motherProffessionId(rs.getString("mother_proffessionid"))
                    .motherNationalityId(rs.getString("mother_nationalityid"))
                    //.motherAgeMarriage(Integer.valueOf(rs.getString("mother_age_marriage")))
                    //.motherAgeDelivery(Integer.valueOf(rs.getString("mother_age_delivery")))
                    .motherNoOfBirthGiven(Integer.valueOf(rs.getString("mother_no_of_birth_given")))
                    .motherMaritalStatusId(rs.getString("mother_maritalstatusid"))
//                    .motherUnmarried(Integer.valueOf(rs.getString("mother_unmarried")))
//                    .motherResLbId(Integer.valueOf(rs.getString("mother_res_lbid")))
//                    .motherResLbCode(Integer.valueOf(rs.getString("mother_res_lb_code_id")))
//                    .motherResPlaceTypeId(Integer.valueOf(rs.getString("mother_res_place_type_id")))
//                    .motherResLbTypeId(Integer.valueOf(rs.getString("mother_res_lb_type_id")))
//                    .motherResDistrictId(Integer.valueOf(rs.getString("mother_res_district_id")))
//                    .motherResStateId(Integer.valueOf(rs.getString("mother_res_state_id")))
//                    .motherResCountryId(Integer.valueOf(rs.getString("mother_res_country_id")))
                    .motherResdnceAddrType(rs.getString("mother_resdnce_addr_type"))
                    .motherResdnceTenentId(rs.getString("mother_resdnce_tenant"))
                    .motherResdncePlaceType(rs.getString("mother_resdnce_placetype"))
                    .motherResdncePlaceEn(rs.getString("mother_resdnce_place_en"))
                    .motherResdncePlaceMl(rs.getString("mother_resdnce_place_ml"))
                    .motherResdnceLbType(rs.getString("mother_resdnce_lbtype"))
                    .motherResdnceDistrictId(rs.getString("mother_resdnce_district"))
                    .motherResdnceStateId(rs.getString("mother_resdnce_state"))
                    .motherResdnceCountryId(rs.getString("mother_resdnce_country"))
                    .birthDtlId(rs.getString("birthdtlid"))
                    .build();
        }
}
