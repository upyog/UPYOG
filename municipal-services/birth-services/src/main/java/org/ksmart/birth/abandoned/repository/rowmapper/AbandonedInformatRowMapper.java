package org.ksmart.birth.abandoned.repository.rowmapper;


import org.ksmart.birth.web.model.InformatDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface AbandonedInformatRowMapper {
        default InformatDetail getInformatDetail(ResultSet rs) throws SQLException {
            return InformatDetail.builder()
                    .informerDesi(rs.getString("pla_auth_officer_desig_id"))
                    .informerAddress(rs.getString("pla_informantsaddress_en"))
                    .infomantMobile(rs.getString("pla_informants_mobileno"))
                    .infomantAadhar(rs.getString("pla_informants_aadhaar_no"))
                    .isDeclarationInfo(rs.getString("pla_is_inform_declare"))
                    .infomantFirstNameEn(rs.getString("pla_informantsname_en"))
                    .infomantinstitution(rs.getString("pla_informants_office_name"))
                    .build();
        }
}
