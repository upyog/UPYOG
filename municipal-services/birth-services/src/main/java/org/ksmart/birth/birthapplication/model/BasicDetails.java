package org.ksmart.birth.birthapplication.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicDetails {
    private String id;
    private Long dateofreport;
    private Long dateofbirth;
    private String timeofbirth;
    private String am_pm;
    private String firstname_en;
    private String firstname_ml;
    private String middlename_en;
    private String middlename_ml;
    private String lastname_en;
    private String lastname_ml;
    private String tenantid;
    private String gender;
    private String remarks_en;
    private String remarks_ml;
    private String aadharno;
    private String esign_user_code;
    private String esign_user_desig_code;
    private Boolean is_adopted;
    private Boolean is_abandoned;
    private Boolean is_multiple_birth;
    private Boolean is_father_info_missing;
    private Boolean is_mother_info_missing;
    private String no_of_alive_birth;
    private String multiplebirthdetid;
    private Boolean is_born_outside;
    private String ot_passportno;
    private String ot_dateofarrival;
    private String registrationno;
    private String registration_date;
    private String adopt_deed_order_no;
    private String adopt_dateoforder_deed;
    private String adopt_issuing_auththority;
    private String adopt_has_agency;
    private String adopt_agency_name;
    private String adopt_agency_address;
    private String createdby;
    private Long createdtime;
    private String lastmodifiedby;
    private Long lastmodifiedtime;
    private Boolean is_stillbirth;
    private String adopt_decree_order_no;
    private String adopt_dateoforder_decree;
    private String adopt_agency_contact_person;
    private String adopt_agency_contact_person_mobileno;
}
