package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPermanentAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegPerAddRowMapper {
    default RegisterBirthPermanentAddress getRegBirthPermanentAddress(ResultSet rs) throws SQLException {
        return RegisterBirthPermanentAddress.builder()
                .id(rs.getString("per_id"))
                .buildingNo(rs.getString("per_buildingno"))
                .houseNo(rs.getString("per_houseno"))
                .resAssoNo(rs.getString("per_res_asso_no"))
                .houseNameEn(rs.getString("per_housename_en"))
                .houseNameMl(rs.getString("per_housename_ml"))
                .otAddress1En(rs.getString("per_ot_address1_en"))
                .otAddress1Ml(rs.getString("per_ot_address1_ml"))
                .otAddress2En(rs.getString("per_ot_address2_en"))
                .otAddress2Ml(rs.getString("per_ot_address2_ml"))
                .localityEn(rs.getString("per_locality_en"))
                .localityMl(rs.getString("per_locality_ml"))
                .cityEn(rs.getString("per_city_en"))
                .cityMl(rs.getString("per_city_ml"))
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
                .sameAsPermanent(rs.getInt("per_same_as_permanent"))
                .permanentAddress(new StringBuilder().append(rs.getString("per_housename_en")==null?"":rs.getString("per_housename_en"))
                        .append(", ")
                        .append(rs.getString("per_res_asso_no")==null?"":rs.getString("per_res_asso_no"))
                        .append(", ")
                        .append(rs.getString("per_poid")==null?"":rs.getString("per_poid"))
                        .append(", ")
                        .append(rs.getString("per_districtid")==null?"":rs.getString("per_districtid"))
                        .append(", ")
                        .append(rs.getString("per_stateid")==null?"":rs.getString("per_stateid"))
                        .append(", ")
                        .append(rs.getString("per_countryid")==null?"":rs.getString("per_countryid")).toString())
                .permanentAddressMl(new StringBuilder().append(rs.getString("per_housename_ml")==null?"":rs.getString("per_housename_ml"))
                        .append(", ")
                        .append(rs.getString("per_res_asso_no")==null?"":rs.getString("per_res_asso_no"))
                        .append(", ")
                        .append(rs.getString("per_poid")==null?"":rs.getString("per_poid"))
                        .append(", ")
                        .append(rs.getString("per_districtid")==null?"":rs.getString("per_districtid"))
                        .append(", ")
                        .append(rs.getString("per_stateid")==null?"":rs.getString("per_stateid"))
                        .append(", ")
                        .append(rs.getString("per_countryid")==null?"":rs.getString("per_countryid")).toString())

                .build();
    }
}
