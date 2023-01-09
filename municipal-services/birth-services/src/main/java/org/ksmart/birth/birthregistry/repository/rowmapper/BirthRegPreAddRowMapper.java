package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPresentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegPreAddRowMapper {
    default RegisterBirthPresentAddress getRegBirthPresentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPresentAddress.builder()
                                        .id(rs.getString("pres_id"))
                                        .buildingNo(rs.getString("pres_buildingno"))
                                        .houseNo(rs.getString("pres_houseno"))
                                        .resAssoNo(rs.getString("pres_res_asso_no"))
                                        .houseNameEn(rs.getString("pres_housename_en"))
                                        .houseNameMl(rs.getString("pres_housename_ml"))
                                        .otAddress1En(rs.getString("pres_ot_address1_en"))
                                        .otAddress1Ml(rs.getString("pres_ot_address1_ml"))
                                        .otAddress2En(rs.getString("pres_ot_address2_en"))
                                        .otAddress2Ml(rs.getString("pres_ot_address2_ml"))
                                        .localityEn(rs.getString("pres_locality_en"))
                                        .localityMl(rs.getString("pres_locality_ml"))
                                        .cityEn(rs.getString("pres_city_en"))
                                        .cityMl(rs.getString("pres_city_ml"))
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
                .presentAddress(new StringBuilder().append(rs.getString("pres_housename_en")==null?"":rs.getString("pres_housename_en"))
                        .append(", ")
                        .append(rs.getString("pres_res_asso_no")==null?"":rs.getString("pres_res_asso_no"))
                        .append(", ")
                        .append(rs.getString("pres_poid")==null?"":rs.getString("pres_poid"))
                        .append(", ")
                        .append(rs.getString("pres_districtid")==null?"":rs.getString("pres_districtid"))
                        .append(", ")
                        .append(rs.getString("pres_stateid")==null?"":rs.getString("pres_stateid"))
                        .append(", ")
                        .append(rs.getString("pres_countryid")==null?"":rs.getString("pres_countryid")).toString())
                .presentAddressMl(new StringBuilder().append(rs.getString("pres_housename_ml")==null?"":rs.getString("pres_housename_ml"))
                        .append(", ")
                        .append(rs.getString("pres_res_asso_no")==null?"":rs.getString("pres_res_asso_no"))
                        .append(", ")
                        .append(rs.getString("pres_poid")==null?"":rs.getString("pres_poid"))
                        .append(", ")
                        .append(rs.getString("pres_districtid")==null?"":rs.getString("pres_districtid"))
                        .append(", ")
                        .append(rs.getString("pres_stateid")==null?"":rs.getString("pres_stateid"))
                        .append(", ")
                        .append(rs.getString("pres_countryid")==null?"":rs.getString("pres_countryid")).toString())

                                        .build();
    }
}
