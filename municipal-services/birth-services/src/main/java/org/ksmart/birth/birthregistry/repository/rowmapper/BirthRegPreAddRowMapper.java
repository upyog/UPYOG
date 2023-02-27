package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPresentAddress;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BirthRegPreAddRowMapper {
    public RegisterBirthPresentAddress getRegBirthPresentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPresentAddress.builder()
                .id(rs.getString("pres_id"))
                .houseNameEn(rs.getString("pres_housename_no_en"))
                .houseNameMl(rs.getString("pres_housename_no_ml"))
                .otAddress1En(rs.getString("pres_ot_address1_en"))
                .otAddress1Ml(rs.getString("pres_ot_address1_ml"))
                .otAddress2En(rs.getString("pres_ot_address2_en"))
                .otAddress2Ml(rs.getString("pres_ot_address2_ml"))
                .villageId(rs.getString("pres_villageid"))
                .tenantId(rs.getString("pres_tenantid"))
                .talukId(rs.getString("pres_talukid"))
                .districtId(rs.getString("pres_districtid"))
                .stateId(rs.getString("pres_stateid"))
                .poId(rs.getString("pres_poid"))
                .pinNo(rs.getString("pres_pinno"))
                .otStateRegionProvinceEn(rs.getString("pres_ot_state_region_province_en"))
                .otStateRegionProvinceMl(rs.getString("pres_ot_state_region_province_ml"))
                .countryId(rs.getString("pres_countryid"))
                .birthDtlId(rs.getString("pres_birthdtlid"))
                .bioAdopt(rs.getString("pres_bio_adopt"))
                .talukName(rs.getString("pres_taluk_name"))
                .villageName(rs.getString("pres_village_name"))
                .otZipcode(rs.getString("pres_ot_zipcode"))
                .localityEn(rs.getString("pres_locality_en"))
                .localityMl(rs.getString("pres_locality_ml"))
                .streetNameEn(rs.getString("pres_street_name_en"))
                .streetNameMl(rs.getString("pres_street_name_ml"))
                .postofficeEn(rs.getString("pres_postoffice_en"))
                .postofficeMl(rs.getString("pres_postoffice_ml"))
                .build();
    }
//
//    private String getAddressEnByResidenceType(ResultSet rs) throws SQLException {
//        String address = "";
//        if (rs.getString("pres_resdnce_addr_type") == CR_RESIDENCE_PLACE_TYPE_OUTSIDE) {
//            address = new StringBuilder()
//                    .append(rs.getString("pres_ot_address1_en") == null ? "" : rs.getString("pres_ot_address1_en"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_address2_en") == null ? "" : rs.getString("pres_ot_address2_en"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_state_region_province_en") == null ? "" : rs.getString("pres_ot_state_region_province_en"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_zipcode") == null ? "" : rs.getString("pres_ot_zipcode"))
//                    .append(", ")
//                    .append(rs.getString("pres_countryid") == null ? "" : rs.getString("pres_countryid")).toString();
//        } else {
//
//            address = new StringBuilder().append(rs.getString("pres_housename_en") == null ? "" : rs.getString("pres_housename_en"))
//                    .append(", ")
//                    .append(rs.getString("pres_res_asso_no") == null ? "" : rs.getString("pres_res_asso_no"))
//                    .append(", ")
//                    .append(rs.getString("pres_poid") == null ? "" : rs.getString("pres_poid"))
//                    .append(", ")
//                    .append(rs.getString("pres_pinno") == null ? "" : rs.getString("pres_pinno"))
//                    .append(", ")
//                    .append(rs.getString("pres_districtid") == null ? "" : rs.getString("pres_districtid"))
//                    .append(", ")
//                    .append(rs.getString("pres_stateid") == null ? "" : rs.getString("pres_stateid"))
//                    .append(", ")
//                    .append(rs.getString("pres_countryid") == null ? "" : rs.getString("pres_countryid")).toString();
//        }
//        return address;
//    }
//
//    private String getAddressMlByResidenceType(ResultSet rs) throws SQLException {
//        String address = "";
//        if (rs.getString("pres_resdnce_addr_type") == CR_RESIDENCE_PLACE_TYPE_OUTSIDE) {
//            address = new StringBuilder()
//                    .append(rs.getString("pres_ot_address1_ml") == null ? "" : rs.getString("pres_ot_address1_ml"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_address2_en") == null ? "" : rs.getString("pres_ot_address2_eng"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_state_region_province_en") == null ? "" : rs.getString("pres_ot_state_region_province_en"))
//                    .append(", ")
//                    .append(rs.getString("pres_ot_zipcode") == null ? "" : rs.getString("pres_ot_zipcode"))
//                    .append(", ")
//                    .append(rs.getString("pres_countryid") == null ? "" : rs.getString("pres_countryid")).toString();
//
//        } else {
//            address = new StringBuilder().append(new StringBuilder().append(rs.getString("pres_housename_ml") == null ? "" : rs.getString("pres_housename_ml"))
//                    .append(", ")
//                    .append(rs.getString("pres_res_asso_no") == null ? "" : rs.getString("pres_res_asso_no"))
//                    .append(", ")
//                    .append(rs.getString("pres_poid") == null ? "" : rs.getString("pres_poid") + "_ML")
//                    .append(", ")
//                    .append(rs.getString("pres_pinno") == null ? "" : rs.getString("pres_pinno"))
//                    .append(", ")
//                    .append(rs.getString("pres_districtid") == null ? "" : rs.getString("pres_districtid") + "_ML")
//                    .append(", ")
//                    .append(rs.getString("pres_stateid") == null ? "" : rs.getString("pres_stateid") + "_ML")
//                    .append(", ")
//                    .append(rs.getString("pres_countryid") == null ? "" : rs.getString("pres_countryid") + "_ML")).toString();
//        }
//        return address;
//    }
}
