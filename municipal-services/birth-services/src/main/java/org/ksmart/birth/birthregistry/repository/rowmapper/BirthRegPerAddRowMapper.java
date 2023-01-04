package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPermanentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegPerAddRowMapper {
    default RegisterBirthPermanentAddress getRegBirthPermanentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPermanentAddress.builder()
                .id(rs.getString("id"))
                //.re(rs.getString("resdnce_addr_type"))
                .buildingNo(rs.getString("buildingno"))
                .houseNo(rs.getString("houseno"))
                .resAssoNo(rs.getString("res_asso_no"))
                .houseNameEn(rs.getString("housename_en"))
                .houseNameMl(rs.getString("housename_ml"))
                .otAddress1En(rs.getString("ot_address1_en"))
                .otAddress1Ml(rs.getString("ot_address1_ml"))
                .otAddress2En(rs.getString("ot_address2_en"))
                .otAddress2Ml(rs.getString("ot_address2_ml"))
                .localityEn(rs.getString("locality_en"))
                .localityMl(rs.getString("locality_ml"))
                .cityEn(rs.getString("city_en"))
                .cityMl(rs.getString("city_ml"))
                .villageId(rs.getString("villageid"))
                .tenantId(rs.getString("tenantid"))
                .talukId(rs.getString("talukid"))
                .districtId(rs.getString("districtid"))
                .stateId(rs.getString("stateid"))
                .poId(rs.getString("poid"))
                .pinNo(rs.getString("pinno"))
                .otStateRegionProvinceEn(rs.getString("ot_state_region_province_en"))
                .otStateRegionProvinceMl(rs.getString("ot_state_region_province_ml"))
                .countryId(rs.getString("countryid"))
                .birthDtlId(rs.getString("birthdtlid"))
                .sameAsPermanent(Integer.valueOf(rs.getString("same_as_permanent")))
                .permanentAddress(rs.getString("housename_en")+rs.getString("res_asso_no")+rs.getString("poid")+rs.getString("districtid")+rs.getString("stateid")+rs.getString("countryid"))
                .permanentAddress(rs.getString("housename_ml")+rs.getString("res_asso_no")+rs.getString("poid")+rs.getString("districtid")+rs.getString("stateid")+rs.getString("countryid"))
                .build();
    }
}
