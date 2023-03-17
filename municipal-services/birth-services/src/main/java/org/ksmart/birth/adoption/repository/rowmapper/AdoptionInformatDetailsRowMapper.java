package org.ksmart.birth.adoption.repository.rowmapper;


import org.ksmart.birth.web.model.InformatDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface AdoptionInformatDetailsRowMapper {
        default InformatDetail getInformantDetail(ResultSet rs) throws SQLException {
            return InformatDetail.builder()

            		.infomantFirstNameEn(rs.getString("pla_oth_auth_officer_name"))
                    .informerDesi(rs.getString("pla_oth_auth_officer_desig"))
                    .informerAddress(rs.getString("pla_informantsaddress_en"))
                    .infomantMobile(rs.getString("pla_informants_mobileno"))
                    .infomantAadhar(rs.getString("pla_informants_aadhaar_no"))
                    .infomantFirstNameEn(rs.getString("pla_is_inform_declare"))
                    .build();
        }
}
