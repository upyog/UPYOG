package org.bel.birthdeath.crdeath.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathRowMapper
     * Rakhi S IKM
     * on  05/12/2022
     */
    
@Component
public class CrDeathRowMapper implements ResultSetExtractor<List<CrDeathDtl>>, BaseRowMapper{
   //RAkhi S on 09.12.2022
    private final CrDeathStatisticalRowMapper rowMapper;

    @Autowired
    CrDeathRowMapper(CrDeathStatisticalRowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }


    @Override
    public List<CrDeathDtl> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<CrDeathDtl> result = new ArrayList<>();
        while (rs.next()) {
   
            // //Rakhi S on 07.12.2022
            result.add(CrDeathDtl.builder()
                                        .id(rs.getString("id"))
                                        .registrationUnit(rs.getString("registrationunit"))
                                        .tenantId(rs.getString("tenantid"))
                                        .correctDeathDateKnown(rs.getInt("correct_death_date_known"))
                                        .dateOfDeath(rs.getLong("dateofdeath"))
                                        .timeOfDeath(rs.getInt("time_of_death"))
                                        .timeOfDeathUnit(rs.getString("timeofdeath_unit"))
                                        .dateOfDeath1(rs.getLong("date_of_death_to"))
                                        .timeOfDeath1(rs.getInt("time_of_death_to"))
                                        .timeOfDeathUnit1(rs.getString("timeofdeath_unit_to"))
                                        .deceasedIdentified(rs.getInt("deceased_identified"))
                                        .deceasedTitle(rs.getString("deceased_title"))                                        
                                        .deceasedFirstNameEn(rs.getString("deceased_firstname_en"))
                                        .deceasedFirstNameMl(rs.getString("deceased_firstname_ml"))
                                        .deceasedMiddleNameEn(rs.getString("deceased_middlename_en"))
                                        .deceasedMiddleNameMl(rs.getString("deceased_middlename_ml"))
                                        .deceasedLastNameEn(rs.getString("deceased_lastname_en"))
                                        .deceasedLastNameMl(rs.getString("deceased_lastname_ml"))
                                        .deceasedAadharNumber(rs.getString("deceased_aadhar_number"))
                                        .deceasedGender(rs.getString("deceased_gender"))
                                        .age(rs.getInt("age"))
                                        .ageUnit(rs.getString("age_unit"))
                                        .dateOfBirth(rs.getLong("dateofbirth"))
                                        .deathPlace(rs.getString("death_place"))
                                        .deathPlaceType(rs.getString("death_place_inst_type"))
                                        .deathPlaceInstId(rs.getString("death_place_inst_id"))
                                        .deathPlaceOfficeName(rs.getString("death_place_office_name"))
                                        .deathPlaceOtherMl(rs.getString("death_place_other_ml"))
                                        .deathPlaceOtherEn(rs.getString("death_place_other_en"))
                                        .informantTitle(rs.getString("informant_title"))
                                        .informantNameEn(rs.getString("informant_name_en"))
                                        .informantNameMl(rs.getString("informant_name_ml"))
                                        .informantAadharSubmitted(rs.getInt("informant_aadhar_submitted"))
                                        .informantAadharNo(rs.getString("informant_aadhar_no"))
                                        .informantMobileNo(rs.getString("informant_mobile_no"))
                                        .generalRemarks(rs.getString("general_remarks"))
                                        .applicationStatus(rs.getString("application_status"))
                                        .placeBurial(rs.getString("place_burial"))
                                        .placeBurialInstitutionType(rs.getString("place_burial_institution_type"))
                                        .placePurialInstitutionName(rs.getString("place_burial_institution_name"))
                                        .registrationNo(rs.getString("registration_no"))
                                        .ipNo(rs.getString("ip_no"))
                                        .opNo(rs.getString("op_no"))
                                        .maleDependentType(rs.getString("male_dependent_type"))
                                        .maleDependentTitle(rs.getString("male_dependent_title"))
                                        .maleDependentNameEn(rs.getString("male_dependent_name_en"))
                                        .maleDependentNameMl(rs.getString("male_dependent_name_ml"))
                                        .maleDependentAadharNo(rs.getString("male_dependent_aadharno"))
                                        .maleDependentMobileNo(rs.getString("male_dependent_mobileno"))
                                        .maleDependentMailId(rs.getString("male_dependent_mailid"))
                                        .femaleDependentType(rs.getString("female_dependent_type"))
                                        .femaleDependentTitle(rs.getString("female_dependent_title"))
                                        .femaleDependentNameEn(rs.getString("female_dependent_name_en"))
                                        .femaleDependentNameMl(rs.getString("female_dependent_name_ml"))
                                        .femaleDependentAadharNo(rs.getString("female_dependent_aadharno"))
                                        .femaleDependentMobileNo(rs.getString("female_dependent_mobileno"))
                                        .femaleDependentMailId(rs.getString("female_dependent_mailid"))
                                        .isvehicle(rs.getInt("isvehicle"))
                                        .vehicleHospitalMl(rs.getString("vehicle_hospital_ml"))
                                        .vehicleHospitalEn(rs.getString("vehicle_hospital_en"))
                                        .vehicleFromplaceMl(rs.getString("vehicle_fromplace_ml"))
                                        .vehicleFromplaceEn(rs.getString("vehicle_fromplace_en"))
                                        .vehicleToPlaceMl(rs.getString("vehicle_toplace_ml"))
                                        .vehicleToPlaceEn(rs.getString("vehicle_toplace_en"))
                                        .vehicleNumber(rs.getString("vehicle_number"))
                                        .deathPlaceWardId(rs.getString("death_place_ward_id"))
                                        .informantAge(rs.getInt("informant_age"))
                                        .vehicleDriverLicenceNo(rs.getString("vehicle_driver_licenceno"))
                                        .deathSignedOfficerDesignation(rs.getString("death_signed_officer_designation"))
                                        .deathSignedOfficerMob(rs.getString("death_place_officer_mobile"))
                                        .deathSignedOfficerAadhaar(rs.getString("death_place_officer_aadhaar"))
                                        .deseasedPassportNo(rs.getString("deseased_passportno"))
                                        .deathApplicationNo(rs.getString("application_no"))
                                        .deathACKNo(rs.getString("ack_no"))   
                                        .auditDetails(getAuditDetails(rs))
                                        //RAKHI S ON 09.12.2022
                                        .statisticalInfo(rowMapper.extractData(rs))
                                        .build());
            
        }

        return result;
    }

   }
