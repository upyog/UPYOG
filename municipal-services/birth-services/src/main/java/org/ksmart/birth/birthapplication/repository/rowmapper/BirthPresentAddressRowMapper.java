package org.ksmart.birth.birthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.birth.BirthPresentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthPresentAddressRowMapper {

    default BirthPresentAddress getBirthPresentAddress(ResultSet rs) throws SQLException {
        return BirthPresentAddress.builder()
                .id(rs.getString("pres_id"))
                .resdnceAddrType(rs.getString("pres_resdnce_addr_type"))
                .resAssoNo(rs.getString("pres_res_asso_no"))
                .houseNameEn(rs.getString("pres_housename_en"))
                .houseNameMl(rs.getString("pres_housename_ml"))
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
                .build();
    }
}
