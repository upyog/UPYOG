package org.bel.birthdeath.crdeath.repository.querybuilder;


import java.util.List;

import javax.validation.constraints.NotNull;

import org.bel.birthdeath.crdeath.web.models.CrDeathSearchCriteria;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathQueryBuilder
     * Rakhi S IKM
     * on  05/12/2022
     */
@Component
public class CrDeathQueryBuilder extends BaseQueryBuilder {
    
     //Rakhi S on 08.12.2022
     private static final String QUERY = new StringBuilder().append("SELECT dt.id, dt.registrationunit, dt.tenantid, dt.correct_death_date_known, dt.dateofdeath, dt.time_of_death, dt.timeofdeath_unit, dt.date_of_death_to, dt.time_of_death_to, dt.timeofdeath_unit_to, dt.deceased_identified") 
                                                            .append("      , dt.deceased_title, dt.deceased_firstname_en, dt.deceased_firstname_ml, dt.deceased_middlename_en, dt.deceased_middlename_ml, dt.deceased_lastname_en, dt.deceased_lastname_ml, dt.deceased_aadhar_number, dt.deceased_gender, dt.age, dt.age_unit, dt.dateofbirth")   
                                                            .append("      , dt.death_place, dt.death_place_inst_type, dt.death_place_inst_id, dt.death_place_office_name, dt.death_place_other_ml, dt.death_place_other_en")        
                                                            // .append("      , dt.death_place, dt.death_place_inst_type, dt.death_place_inst_id, dt.death_place_office_name, dt.death_place_other_ml, dt.death_place_other_en")
                                                            .append("      , dt.informant_title, dt.informant_name_en, dt.informant_name_ml, dt.informant_aadhar_submitted, dt.informant_aadhar_no, dt.informant_mobile_no, dt.general_remarks")
                                                            .append("      , dt.application_status, dt.submitted_on, dt.created_by, dt.createdtime, dt.lastmodifiedby, dt.lastmodifiedtime")
                                                            .append("      , dt.place_burial, dt.place_burial_institution_type, dt.place_burial_institution_name, dt.registration_no, dt.ip_no, dt.op_no")
                                                            .append("      , dt.male_dependent_type, dt.male_dependent_title, dt.male_dependent_name_en, dt.male_dependent_name_ml, dt.male_dependent_aadharno, dt.male_dependent_mobileno, dt.male_dependent_mailid")
                                                            .append("      , dt.female_dependent_type, dt.female_dependent_title, dt.female_dependent_name_en, dt.female_dependent_name_ml, dt.female_dependent_aadharno, dt.female_dependent_mobileno, dt.female_dependent_mailid")
                                                            .append("      , dt.isvehicle, dt.vehicle_hospital_ml, dt.vehicle_hospital_en, dt.vehicle_fromplace_ml, dt.vehicle_fromplace_en, dt.vehicle_toplace_ml, dt.vehicle_toplace_en, dt.vehicle_number, dt.death_place_ward_id, dt.informant_age, dt.vehicle_driver_licenceno")
                                                            .append("      , dt.death_signed_officer_designation, dt.death_place_officer_mobile, dt.death_place_officer_aadhaar, dt.deseased_passportno, dt.application_no, dt.file_no, dt.ack_no")
                                                            .append("      , stat.id statid, stat.death_dtl_id, stat.tenantid stattenantid, stat.residencelocalbody, stat.residence_place_type, stat.residencedistrict, stat.residencestate, stat.religion, stat.religion_other, stat.occupation, stat.occupation_other, stat.medical_attention_type")
                                                            .append("      , stat.death_medically_certified, stat.death_cause_main, stat.death_cause_sub, stat.death_cause_other, stat.death_during_delivery, stat.smoking_num_years, stat.tobacco_num_years, stat.arecanut_num_years, stat.alcohol_num_years")
                                                            .append("      , stat.createdby, stat.createdtime, stat.lastmodifiedby, stat.lastmodifiedtime, stat.nationality")
                                                            .append(" FROM eg_death_dtls dt") 
                                                            .append(" INNER JOIN eg_death_statistical_dtls stat ON dt.id = stat.death_dtl_id AND dt.tenantid = stat.tenantid")
                                                            .toString();

    public String getDeathSearchQuery(@NotNull CrDeathSearchCriteria criteria,
                                                           @NotNull List<Object> preparedStmtValues, Boolean isCount) {
         
         StringBuilder query = new StringBuilder(QUERY);
    
         System.out.println("RAkhiPreparedStmt"+preparedStmtValues);
         System.out.println("idCheck"+criteria.getId());
         addFilter("dt.id", criteria.getId(), query, preparedStmtValues);
         addFilter("dt.tenantid", criteria.getTenantId(), query, preparedStmtValues);
         addFilter("dt.deceased_aadhar_number", criteria.getAadhaarNo(), query, preparedStmtValues);                                                   
         return query.toString();
       }                                                  
    
}
