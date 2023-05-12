package org.ksmart.birth.bornoutside.repository.rowmapper;

import org.ksmart.birth.web.model.bornoutside.BornOutsideStatInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BornOutsideStatRowMapper {
        default BornOutsideStatInfo getStatInformation(ResultSet rs) throws SQLException {
            return BornOutsideStatInfo.builder()
                    .informarNameEn(rs.getString("pla_informantsname_en"))
                    .informarAadhar(rs.getString("pla_informants_aadhaar_no"))
                    .informarMobile(rs.getString("pla_informants_mobileno"))
                    .relation(rs.getString("pla_relation"))
                    .deliveryMethods(rs.getString("stat_delivery_method"))
                    .birthWeight(rs.getDouble("stat_weight_of_child"))
                    .medicalAttensionSub(rs.getString("stat_nature_of_medical_attention"))
                    .pregnancyDuration(rs.getInt("stat_duration_of_pregnancy_in_week"))
                    .informarAddress(rs.getString("pla_informantsaddress_en"))
                    .orderofChildren(rs.getInt("stat_mother_order_cur_child"))
                    .build();
        }
}
