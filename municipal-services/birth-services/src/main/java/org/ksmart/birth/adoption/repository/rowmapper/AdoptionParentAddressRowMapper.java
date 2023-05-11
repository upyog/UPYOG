package org.ksmart.birth.adoption.repository.rowmapper;

import org.ksmart.birth.web.model.ParentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface AdoptionParentAddressRowMapper {
    default ParentAddress getKsmartBirthParentAddress(ResultSet rs) throws SQLException {
    	 
        return ParentAddress.builder()
                .countryIdPresent(rs.getString("pres_countryid"))
                .stateIdPresent(rs.getString("pres_stateid"))
                .districtIdPresent(rs.getString("pres_districtid"))
                .pinNoPresent(rs.getString("pres_pinno"))
                .presentaddressCountry(rs.getString("pres_countryid"))
                .localityEnPresent(rs.getString("pres_locality_en"))
                .localityMlPresent(rs.getString("pres_locality_ml"))
                .streetNameEnPresent(rs.getString("pres_street_name_en"))
                .streetNameMlPresent(rs.getString("pres_street_name_ml"))
                .presentInsideKeralaStreetNameMl(rs.getString("pres_street_name_ml"))
                .presentInsideKeralaStreetNameEn(rs.getString("pres_street_name_en"))
                .houseNameNoEnPresent(rs.getString("pres_housename_no_en"))
                .houseNameNoMlPresent(rs.getString("pres_housename_no_ml"))
                .villageNamePresent(rs.getString("pres_village_name"))
                .poNoPresent(rs.getString("pres_pinno"))
                
                
               
                
                .presentInsideKeralaLBName(rs.getString("pres_tenantid"))
                .presentInsideKeralaTaluk(rs.getString("pres_talukid"))
                .presentInsideKeralaPostOffice(rs.getString("pres_poid"))
                .presentInsideKeralaVillage(rs.getString("pres_villageid"))
                .presentWardNo(rs.getString("pres_ward_code"))
                .presentOutsideKeralaPostOfficeEn(rs.getString("pres_postoffice_en"))
                .presentOutsideKeralaPostOfficeMl(rs.getString("pres_postoffice_ml"))
                .presentOutsideKeralaTalukName(rs.getString("pres_taluk_name"))
                .townOrVillagePresent(rs.getString("stat_mother_resdnce_placetype"))
                
                
                .presentOutsideKeralaCityVilgeEn(rs.getString("stat_mother_resdnce_placetype"))
                .presentOutSideIndiaAdressEn(rs.getString("pres_ot_address1_en"))
                .presentOutSideIndiaAdressMl(rs.getString("pres_ot_address1_ml"))
                .presentOutSideIndiaAdressEnB(rs.getString("pres_ot_address2_en"))
                .presentOutSideIndiaAdressMlB(rs.getString("pres_ot_address2_ml"))
                .presentOutSideIndiaProvinceEn(rs.getString("pres_ot_state_region_province_en"))
                .presentOutSideIndiaProvinceMl(rs.getString("pres_ot_state_region_province_ml"))
                .presentOutSideIndiaadrsCityTown(rs.getString("stat_mother_resdnce_placetype"))
                
                
                .countryIdPermanent(rs.getString("per_countryid"))
                .stateIdPermanent(rs.getString("per_stateid"))
                .districtIdPermanent(rs.getString("per_districtid"))
                .pinNoPermanent(rs.getString("per_pinno"))
                .poNoPermanent(rs.getString("per_pinno"))
                .localityEnPermanent(rs.getString("per_locality_en"))
                .localityMlPermanent(rs.getString("per_locality_ml"))
                .streetNameEnPermanent(rs.getString("per_street_name_en"))
                .streetNameMlPermanent(rs.getString("per_street_name_ml"))
                .houseNameNoEnPermanent(rs.getString("per_housename_no_en"))
                .houseNameNoMlPermanent(rs.getString("per_housename_no_ml"))
                .villageNamePermanent(rs.getString("per_village_name"))
                
                .permntInKeralaAdrLBName(rs.getString("per_tenantid"))
                .permntInKeralaAdrTaluk(rs.getString("per_talukid"))
                .permntInKeralaAdrPostOffice(rs.getString("per_poid"))
                .permntInKeralaAdrPincode(rs.getString("per_pinno"))
                .permntInKeralaAdrVillage(rs.getString("per_villageid"))
                .permntInKeralaWardNo(rs.getString("per_ward_code"))
                
                
                .permntOutsideKeralaTaluk(rs.getString("per_taluk_name"))
                .permntOutsideKeralaCityVilgeEn(rs.getString("stat_mother_resdnce_placetype"))
                .permntOutsideKeralaPostOfficeEn(rs.getString("per_postoffice_en"))
                .permntOutsideKeralaPostOfficeMl(rs.getString("per_postoffice_ml"))
                .isPrsentAddress(rs.getInt("per_same_as_present")==1?true:false)
                .isPrsentAddressInt(rs.getInt("per_same_as_present"))
                
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




