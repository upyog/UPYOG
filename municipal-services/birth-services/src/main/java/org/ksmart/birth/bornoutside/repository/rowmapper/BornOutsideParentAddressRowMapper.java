package org.ksmart.birth.bornoutside.repository.rowmapper;

import org.ksmart.birth.web.model.ParentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BornOutsideParentAddressRowMapper {
    default ParentAddress getKsmartBirthParentAddress(ResultSet rs) throws SQLException {
        return ParentAddress.builder()
                //present
                .countryIdPresent(rs.getString("pres_countryid"))
                .presentOutSideCountry(rs.getString("pres_countryid"))
                .presentOutSideIndiaAdressEn(rs.getString("pres_ot_address1_en"))
                .presentOutSideIndiaAdressMl(rs.getString("pres_ot_address1_ml"))
                .presentOutSideIndiaAdressEnB(rs.getString("pres_ot_address2_en"))
                .presentOutSideIndiaAdressMlB(rs.getString("pres_ot_address2_ml"))
                .presentOutSideIndiaProvinceEn(rs.getString("pres_ot_state_region_province_en"))
                .presentOutSideIndiaProvinceMl(rs.getString("pres_ot_state_region_province_ml"))
                .presentOutSideIndiaadrsCityTown(rs.getString("stat_mother_resdnce_placetype"))
                .presentOutSideIndiaadrsVillage(rs.getString("pres_village_name"))
                .villageNamePresent(rs.getString("pres_village_name"))
                .presentOutSideIndiaPostCode(rs.getString("pres_ot_zipcode"))
                .isPrsentAddress(rs.getBoolean("per_same_as_present"))


                //permanent
                .countryIdPermanent(rs.getString("per_countryid"))
                .stateIdPermanent(rs.getString("per_stateid"))
                .districtIdPermanent(rs.getString("per_districtid"))
                .poNoPermanent(rs.getString("per_poid"))
                .localityEnPermanent(rs.getString("per_locality_en"))
                .localityMlPermanent(rs.getString("per_locality_ml"))
                .streetNameEnPermanent(rs.getString("per_street_name_en"))
                .streetNameMlPermanent(rs.getString("per_street_name_ml"))
                .houseNameNoEnPermanent(rs.getString("per_housename_no_en"))
                .houseNameNoMlPermanent(rs.getString("per_housename_no_ml"))
                .villageNamePermanent(rs.getString("per_village_name"))
                .permntInKeralaAdrPostOffice(rs.getString("per_poid"))
                .permntInKeralaAdrPincode(rs.getString("per_pinno"))
                .permntInKeralaAdrLBName(rs.getString("per_tenantid"))
                .permntInKeralaAdrTaluk(rs.getString("per_talukid"))
                .permntInKeralaAdrVillage(rs.getString("per_villageid"))
                .permntInKeralaWardNo(rs.getString("per_ward_code"))

                .presentUuid(rs.getString("pres_id"))
                .permanentUuid(rs.getString("per_id"))
                .build();

    }
}




