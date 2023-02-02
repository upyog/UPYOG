package org.ksmart.birth.birthregistry.repository.rowmapper;


import static org.ksmart.birth.utils.BirthConstants.*;

import org.ksmart.birth.birthregistry.model.RegisterBirthPermanentAddress;

import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BirthRegPerAddRowMapper {
    public RegisterBirthPermanentAddress getRegBirthPermanentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPermanentAddress.builder()
                .id(rs.getString("per_id"))
                .resdnceAddrType(rs.getString("per_resdnce_addr_type"))
                .resAssoNo(rs.getString("per_res_asso_no"))
                .houseNameEn(rs.getString("per_housename_en"))
                .houseNameMl(rs.getString("per_housename_ml"))
                .otAddress1En(rs.getString("per_ot_address1_en"))
                .otAddress1Ml(rs.getString("per_ot_address1_ml"))
                .otAddress2En(rs.getString("per_ot_address2_en"))
                .otAddress2Ml(rs.getString("per_ot_address2_ml"))
                .villageId(rs.getString("per_villageid"))
                .tenantId(rs.getString("per_tenantid"))
                .talukId(rs.getString("per_talukid"))
                .districtId(rs.getString("per_districtid"))
                .stateId(rs.getString("per_stateid"))
                .poId(rs.getString("per_poid"))
                .pinNo(rs.getString("per_pinno"))
                .otStateRegionProvinceEn(rs.getString("per_ot_state_region_province_en"))
                .otStateRegionProvinceMl(rs.getString("per_ot_state_region_province_ml"))
                .countryId(rs.getString("per_countryid"))
                .birthDtlId(rs.getString("per_birthdtlid"))
                .sameAsPresent(rs.getInt("per_same_as_present"))
                .bioAdopt(rs.getString("per_bio_adopt"))
                .resAssoNoMl(rs.getString("per_countryid"))
                .talukName(rs.getString("per_taluk_name"))
                .villageName(rs.getString("per_village_name"))
                .wardCode(rs.getString("per_ward_code"))
                .doorNo(rs.getString("per_doorno"))
                .subNo(rs.getString("per_subno"))
                .otZipcode(rs.getString("per_ot_zipcode"))
                .mainPlaceEn(rs.getString("per_main_place_en"))
                .streetLocalityAreaEn(rs.getString("per_street_locality_area_en"))
                .mainPlaceMl(rs.getString("per_main_place_ml"))
                .streetLocalityAreaMl(rs.getString("per_street_locality_area_ml"))
                .permanentAddress(getAddressEnByResidenceType(rs))
                .permanentAddressMl(getAddressMlByResidenceType(rs))
                .build();
    }

    private String getAddressEnByResidenceType(ResultSet rs) throws SQLException {
        String address = "";
        if (rs.getString("per_resdnce_addr_type") == CR_RESIDENCE_PLACE_TYPE_OUTSIDE) {
            address = new StringBuilder()
                    .append(rs.getString("per_housename_en") == null ? "" : rs.getString("per_housename_en")+',')
                    .append(rs.getString("per_res_asso_no") == null ? "" : rs.getString("per_res_asso_no")+',')
                    .append(rs.getString("per_main_place_en") == null ? "" : rs.getString("per_main_place_en")+',')
                    .append(rs.getString("per_street_locality_area_en") == null ? "" : rs.getString("per_street_locality_area_en")+',')
                    .append(rs.getString("per_ot_state_region_province_en") == null ? "" : rs.getString("per_ot_state_region_province_en")+',')
                    .append(rs.getString("per_ot_zipcode") == null ? "" : rs.getString("per_ot_zipcode")+',')
                    .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid")).toString();
        } else {

            address = new StringBuilder()
                    .append(rs.getString("per_housename_en") == null ? "" : rs.getString("per_housename_en")+',')
                    .append(rs.getString("per_res_asso_no") == null ? "" : rs.getString("per_res_asso_no")+',')
                    .append(rs.getString("per_main_place_en") == null ? "" : rs.getString("per_main_place_en")+',')
                    .append(rs.getString("per_street_locality_area_en") == null ? "" : rs.getString("per_street_locality_area_en")+',')
                    .append(rs.getString("per_poid") == null ? "" : rs.getString("per_poid")+',')
                    .append(rs.getString("per_pinno") == null ? "" : rs.getString("per_pinno")+',')
                    .append(rs.getString("per_districtid") == null ? "" : rs.getString("per_districtid")+',')
                    .append(rs.getString("per_stateid") == null ? "" : rs.getString("per_stateid")+',')
                    .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid")).toString();
        }
        return address;
    }

    private String getAddressMlByResidenceType(ResultSet rs) throws SQLException {
        String address = "";
        if (rs.getString("per_resdnce_addr_type") == CR_RESIDENCE_PLACE_TYPE_OUTSIDE) {
            address = new StringBuilder()
                    .append(rs.getString("per_housename_ml") == null ? "" : rs.getString("per_housename_ml")+',')
                    .append(rs.getString("per_res_asso_no") == null ? "" : rs.getString("per_res_asso_no")+',')
                    .append(rs.getString("per_main_place_ml") == null ? "" : rs.getString("per_main_place_ml")+',')
                    .append(rs.getString("per_street_locality_area_ml") == null ? "" : rs.getString("per_street_locality_area_ml")+',')
                    .append(rs.getString("per_ot_state_region_province_ml") == null ? "" : rs.getString("per_ot_state_region_province_ml")+',')
                    .append(rs.getString("per_ot_zipcode") == null ? "" : rs.getString("per_ot_zipcode")+',')
                    .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid")).toString();

        } else {
            address = new StringBuilder()
                    .append(rs.getString("per_housename_ml") == null ? "" : rs.getString("per_housename_ml")+',')
                    .append(rs.getString("per_res_asso_no") == null ? "" : rs.getString("per_res_asso_no")+',')
                    .append(rs.getString("per_main_place_ml") == null ? "" : rs.getString("per_main_place_ml")+',')
                    .append(rs.getString("per_street_locality_area_ml") == null ? "" : rs.getString("per_street_locality_area_ml")+',')
                    .append(rs.getString("per_poid") == null ? "" : rs.getString("per_poid") + "_ML"+',')
                    .append(rs.getString("per_pinno") == null ? "" : rs.getString("per_pinno")+',')
                    .append(rs.getString("per_districtid") == null ? "" : rs.getString("per_districtid") + "_ML"+',')
                    .append(rs.getString("per_stateid") == null ? "" : rs.getString("per_stateid") + "_ML"+',')
                    .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid") + "_ML").toString();
        }
        return address;
    }
}

