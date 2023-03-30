package org.ksmart.birth.birthregistry.repository.rowmapperforcorrectionapplication;


import org.ksmart.birth.birthregistry.model.RegisterBirthPermanentAddress;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RegisterPermanentAddressRowMapperForCorr {
    public RegisterBirthPermanentAddress getRegBirthPermanentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPermanentAddress.builder()
                .id(rs.getString("per_id"))
                .houseNameEn(rs.getString("per_housename_no_en"))
                .houseNameMl(rs.getString("per_housename_no_ml"))
                .otAddress1En(rs.getString("per_ot_address1_en"))
                .otAddress1Ml(rs.getString("per_ot_address1_ml"))
                .otAddress2En(rs.getString("per_ot_address2_en"))
                .otAddress2Ml(rs.getString("per_ot_address2_ml"))
//                .villageId(rs.getString("per_villageid"))
//                .tenantId(rs.getString("per_tenantid"))
//                .talukId(rs.getString("per_talukid"))
//                .districtId(rs.getString("per_districtid"))
//                .stateId(rs.getString("per_stateid"))
                .poId(rs.getString("per_poid"))
                .pinNo(rs.getString("per_pinno"))
//                .otStateRegionProvinceEn(rs.getString("per_ot_state_region_province_en"))
//                .otStateRegionProvinceMl(rs.getString("per_ot_state_region_province_ml"))
//                .countryId(rs.getString("per_countryid"))
//                .birthDtlId(rs.getString("per_birthdtlid"))
//                .sameAsPresent(rs.getInt("per_same_as_present"))
//                .bioAdopt(rs.getString("per_bio_adopt"))
//                .talukName(rs.getString("per_taluk_name"))
//                .villageName(rs.getString("per_village_name"))
                .otZipcode(rs.getString("per_ot_zipcode"))
                .localityEn(rs.getString("per_locality_en"))
                .localityMl(rs.getString("per_locality_ml"))
                .streetNameEn(rs.getString("per_street_name_en"))
                .streetNameMl(rs.getString("per_street_name_ml"))
                .postofficeEn(rs.getString("per_postoffice_en"))
                .postofficeMl(rs.getString("per_postoffice_ml"))
//                .familyEmailid(rs.getString("per_family_emailid"))
//                .familyMobileno(rs.getString("per_family_mobileno"))
                .build();
    }


}

