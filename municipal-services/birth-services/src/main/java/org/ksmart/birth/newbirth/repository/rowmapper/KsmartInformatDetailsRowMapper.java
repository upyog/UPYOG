package org.ksmart.birth.newbirth.repository.rowmapper;


import org.ksmart.birth.web.model.InformatDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartInformatDetailsRowMapper {
        default InformatDetail getKsmartInformatDetail(ResultSet rs) throws SQLException {
            return InformatDetail.builder()

                    .informerAddress(rs.getString("pla_informantsaddress_en"))
                    .infomantMobile(rs.getString("pla_informants_mobileno"))
                    .infomantAadhar(rs.getString("pla_informants_aadhaar_no"))
                    .infomantFirstNameEn(rs.getString("pla_informantsname_en"))
                    .build();
        }
}
