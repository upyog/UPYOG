package org.ksmart.birth.correction.repository.rowmapper;

import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.correction.CorrectionAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CorrectionParentAddressRowMapper {
    default CorrectionAddress getCorrectionParentAddress(ResultSet rs) throws SQLException {
        return CorrectionAddress.builder()
//                .countryIdPresent(rs.getString("pres_countryid"))
//                .stateIdPresent(rs.getString("pres_stateid"))
//                .districtIdPresent(rs.getString("pres_districtid"))
//                .poNoPresent(rs.getString("pres_poid"))
//                .presentaddressCountry(rs.getString("pres_countryid"))
//                .localityEnPresent(rs.getString("pres_locality_en"))
//                .localityMlPresent(rs.getString("pres_locality_ml"))
//                .streetNameEnPresent(rs.getString("pres_street_name_en"))
//                .streetNameMlPresent(rs.getString("pres_street_name_ml"))
//                .houseNameNoEnPresent(rs.getString("pres_housename_no_en"))
//                .houseNameNoMlPresent(rs.getString("pres_housename_no_ml"))
//                .presentInsideKeralaLBName(rs.getString("pres_tenantid"))
//                .presentInsideKeralaPostOffice(rs.getString("pres_poid"))
//                .presentWardNo(rs.getString("pres_ward_code"))
//                .presentOutsideKeralaPostOfficeEn(rs.getString("pres_postoffice_en"))
//                .presentOutsideKeralaPostOfficeMl(rs.getString("pres_postoffice_ml"))
//
//
//                .presentOutSideIndiaAdressEn(rs.getString("pres_ot_address1_en"))
//                .presentOutSideIndiaAdressMl(rs.getString("pres_ot_address1_ml"))
//                .presentOutSideIndiaAdressEnB(rs.getString("pres_ot_address2_en"))
//                .presentOutSideIndiaAdressMlB(rs.getString("pres_ot_address2_ml"))
//                .presentOutSideIndiaProvinceEn(rs.getString("pres_ot_state_region_province_en"))
//                .presentOutSideIndiaProvinceMl(rs.getString("pres_ot_state_region_province_ml"))
//
//                .countryIdPermanent(rs.getString("per_countryid"))
//                .stateIdPermanent(rs.getString("per_stateid"))
//                .districtIdPermanent(rs.getString("per_districtid"))
//                .poNoPermanent(rs.getString("per_poid"))
                .permanentLocalityNameEn(rs.getString("per_locality_en"))
                .permanentLocalityNameMl(rs.getString("per_locality_ml"))
                .permanentStreetNameEn(rs.getString("per_street_name_en"))
                .permanentStreetNameMl(rs.getString("per_street_name_ml"))
                .permanentHouseNameEn(rs.getString("per_housename_no_en"))
                .permanentHouseNameMl(rs.getString("per_housename_no_ml"))
//                .villageNamePermanent(rs.getString("per_village_name"))
//                .permntInKeralaAdrPostOffice(rs.getString("per_poid"))
//                .permntInKeralaAdrPincode(rs.getString("per_pinno"))
//                .permntInKeralaAdrLBName(rs.getString("per_tenantid"))
//                .permntInKeralaWardNo(rs.getString("per_ward_code"))
//
//                .permntOutsideKeralaPostOfficeEn(rs.getString("per_postoffice_en"))
//                .permntOutsideKeralaPostOfficeMl(rs.getString("per_postoffice_ml"))
//                .isPrsentAddress(rs.getInt("per_same_as_present")==1?true:false)
//                .isPrsentAddressInt(rs.getInt("per_same_as_present"))
//
//                .permntOutsideIndiaLineoneEn(rs.getString("per_ot_address1_en"))
//                .permntOutsideIndiaLineoneMl(rs.getString("per_ot_address1_ml"))
//                .permntOutsideIndiaLinetwoEn(rs.getString("per_ot_address2_en"))
//                .permntOutsideIndiaLinetwoMl(rs.getString("per_ot_address2_ml"))
//                .permntOutsideIndiaprovinceEn(rs.getString("per_ot_state_region_province_en"))
//                .permanentOutsideIndiaPostCode(rs.getString("per_ot_zipcode"))
//
//                .presentUuid(rs.getString("pres_id"))
                .permanentUuid(rs.getString("per_id"))
                .build();

    }
}




