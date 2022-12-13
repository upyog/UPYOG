package org.ksmart.birth.birthregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterBirthStatiticalInformation {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("weight_of_child")
    private Long weightOfChild;

    @JsonProperty("duration_of_pregnancy_in_week")
    private Integer durationOfPregnancyInWeek;

    @Size(max = 64)
    @JsonProperty("nature_of_medical_attention")
    private String natureOfMedicalAttention;

    @Size(max = 64)
    @JsonProperty("way_of_pregnancy")
    private String wayOfPregnancy;

    @Size(max = 64)
    @JsonProperty("delivery_method")
    private String deliveryMethod;

    @Size(max = 1000)
    @JsonProperty("deliverytypeothers_en")
    private String deliveryTypeOthersEn;

    @Size(max = 1000)
    @JsonProperty("deliverytypeothers_ml")
    private String deliveryTypeOthersMl;

    @Size(max = 64)
    @JsonProperty("religionid")
    private String religionId;

    @Size(max = 64)
    @JsonProperty("father_nationalityid")
    private String fatherNationalityId;

    @Size(max = 64)
    @JsonProperty("father_educationid")
    private String fatherEducationId;

    @Size(max = 64)
    @JsonProperty("father_education_subid")
    private String fatherEducationSubId;

    @Size(max = 64)
    @JsonProperty("father_proffessionid")
    private String fatherProffessionId;

    @Size(max = 64)
    @JsonProperty("mother_educationid")
    private String motherEducationId;

    @Size(max = 64)
    @JsonProperty("mother_education_subid")
    private String motherEducationSubId;

    @Size(max = 64)
    @JsonProperty("mother_proffessionid")
    private String motherProffessionId;

    @Size(max = 64)
    @JsonProperty("mother_nationalityid")
    private String motherNationalityId;

    @JsonProperty("mother_age_marriage")
    private Integer motherAgeMarriage;

    @JsonProperty("mother_age_delivery")
    private Integer motherAgeDelivery;

    @JsonProperty("mother_no_of_birth_given")
    private Integer motherNoOfBirthGiven;

    @JsonProperty("mother_order_of_cur_delivery")
    private Integer motherOrderOfCurDelivery;

    @JsonProperty("mother_order_cur_child")
    private Integer motherOrderCurChild;

    @Size(max = 64)
    @JsonProperty("mother_maritalstatusid")
    private String motherMaritalStatusId;

    @JsonProperty("mother_unmarried")
    private Integer motherUnmarried;

    @JsonProperty("mother_res_lbid")
    private Integer motherResLbId;

    @JsonProperty("mother_res_no_of_years")
    private Integer motherResNoOfYears;

    @JsonProperty("mother_res_lb_code")
    private Integer motherResLbCode;

    @JsonProperty("mother_res_place_type_id")
    private Integer motherResPlaceTypeId;

    @JsonProperty("mother_res_lb_type_id")
    private Integer motherResLbTypeId;

    @JsonProperty("mother_res_district_id")
    private Integer motherResDistrictId;

    @JsonProperty("mother_res_state_id")
    private Integer motherResStateId;

    @JsonProperty("mother_res_country_id")
    private Integer motherResCountryId;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_addr_type")
    private String motherResdnceAddrType;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_tenentid")
    private String motherResdnceTenentId;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_placetype")
    private String motherResdncePlaceType;

    @Size(max = 2500)
    @JsonProperty("mother_resdnce_place_en")
    private String motherResdncePlaceEn;

    @Size(max = 2500)
    @JsonProperty("mother_resdnce_place_ml")
    private String motherResdncePlaceMl;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_lbtype")
    private String motherResdnceLbType;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_districtid")
    private String motherResdnceDistrictId;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_stateid")
    private String motherResdnceStateId;

    @Size(max = 64)
    @JsonProperty("mother_resdnce_countryid")
    private String motherResdnceCountryId;

    @Size(max = 64)
    @JsonProperty("birthdtlid")
    private String birthDtlId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
