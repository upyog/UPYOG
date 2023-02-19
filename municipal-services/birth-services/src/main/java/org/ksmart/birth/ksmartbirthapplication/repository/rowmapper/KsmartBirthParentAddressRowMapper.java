package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;

import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthParentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartBirthParentAddressRowMapper {
    default KsmartBirthParentAddress getKsmartBirthParentAddress(ResultSet rs) throws SQLException {
        return KsmartBirthParentAddress.builder()
                .countryIdPresent(rs.getString("pres_countryid"))
                .stateIdPresent(rs.getString("pres_stateid"))
                .districtIdPresent(rs.getString("pres_districtid"))
                .pinNoPresent(rs.getString("pres_pinno"))
                .localityEnPresent(rs.getString("pres_locality_en"))
                .localityMlPresent(rs.getString("pres_locality_ml"))
                .streetNameEnPresent(rs.getString("pres_street_name_en"))
                .streetNameMlPresent(rs.getString("pres_street_name_ml"))
                .houseNameNoEnPresent(rs.getString("pres_housename_no_en"))
                .houseNameNoMlPresent(rs.getString("pres_housename_no_ml"))
                .villageNamePresent(rs.getString("pres_village_name"))
                .poNoPresent(rs.getString("pres_pinno"))
                .countryIdPermanent(rs.getString("per_countryid"))
                .stateIdPermanent(rs.getString("per_stateid"))
                .districtIdPermanent(rs.getString("per_districtid"))
                .pinNoPermanent(rs.getString("per_pinno"))
                .localityEnPermanent(rs.getString("per_locality_en"))
                .localityMlPermanent(rs.getString("per_locality_ml"))
                .streetNameEnPermanent(rs.getString("per_street_name_en"))
                .streetNameMlPermanent(rs.getString("per_street_name_ml"))
                .houseNameNoEnPermanent(rs.getString("per_housename_no_en"))
                .houseNameNoMlPermanent(rs.getString("per_housename_no_ml"))
                .villageNamePermanent(rs.getString("per_village_name"))
                .poNoPermanent(rs.getString("per_pinno"))
                .presentInsideKeralaLBName(rs.getString("pres_tenantid"))
                .presentInsideKeralaTaluk(rs.getString("pres_talukid"))
                .presentOutsideKeralaTalukName(rs.getString("pres_taluk_name"))
                .presentOutsideKeralaCityVilgeEn(rs.getString("stat_mother_resdnce_placetype"))
                .presentOutSideIndiaAdressEn(rs.getString("pres_ot_address1_en"))
                .presentOutSideIndiaAdressMl(rs.getString("pres_ot_address1_ml"))
                .presentOutSideIndiaAdressEnB(rs.getString("pres_ot_address2_en"))
                .presentOutSideIndiaAdressMlB(rs.getString("pres_ot_address2_ml"))
                .presentOutSideIndiaProvinceEn(rs.getString("pres_ot_state_region_province_en"))
                .presentOutSideIndiaProvinceMl(rs.getString("pres_ot_state_region_province_ml"))
                .presentOutSideIndiaadrsCityTown(rs.getString("stat_mother_resdnce_placetype"))
                //.isPrsentAddress(rs.getInt("per_same_as_present"))
                .permntInKeralaAdrLBName(rs.getString("per_tenantid"))
                .permntInKeralaAdrTaluk(rs.getString("per_talukid"))
                .permntInKeralaWardNo(rs.getString("per_ward_code"))
                .permntOutsideKeralaTaluk(rs.getString("per_taluk_name"))
                .permntOutsideKeralaCityVilgeEn(rs.getString("stat_mother_resdnce_placetype"))
                .permntOutsideIndiaLineoneEn(rs.getString("per_ot_address1_en"))
                .permntOutsideIndiaLineoneMl(rs.getString("per_ot_address1_ml"))
                .permntOutsideIndiaLinetwoEn(rs.getString("per_ot_address2_en"))
                .permntOutsideIndiaLinetwoMl(rs.getString("per_ot_address2_ml"))
                .permntOutsideIndiaprovinceEn(rs.getString("per_ot_state_region_province_en"))
                .permanentOutsideIndiaPostCode(rs.getString("per_ot_zipcode"))
                .presentUuid(rs.getString("pres_id"))
                .permanentUuid(rs.getString("per_id"))
                .build();

    }
}




